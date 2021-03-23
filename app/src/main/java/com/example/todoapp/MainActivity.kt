package com.example.todoapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.tasks.AllTasksAdapter
import com.example.todoapp.tasks.data.TaskList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

lateinit var firebaseDatabase: DatabaseReference
var todoList: MutableList<TaskList>? = null
lateinit var adapter: AllTasksAdapter

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<View>(R.id.buttonAddNewTaskList) as FloatingActionButton
        recyclerView = findViewById<View>(R.id.allTasksRecyclerView) as RecyclerView

        fab.setOnClickListener {
            addNewListDialog()
        }

        firebaseDatabase = FirebaseDatabase.getInstance().reference
        todoList = mutableListOf()
        adapter = AllTasksAdapter(this, todoList!!)
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        firebaseDatabase.orderByKey().addListenerForSingleValueEvent(listener)
    }

    private var listener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            addDataToList(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }

        private fun addDataToList(dataSnapshot: DataSnapshot) {
            val items = dataSnapshot.children.iterator()

            if (items.hasNext()) {
                val todoListIndex = items.next()
                val itemsIterator = todoListIndex.children.iterator()

                while (itemsIterator.hasNext()) {
                    val currentItem = itemsIterator.next()
                    val todoItem = TaskList.create()
                    val map = currentItem.value as HashMap<*, *>

                    todoItem.objectId = currentItem.key
                    todoItem.listTitle = map["listTitle"] as String
                    todoItem.progress = map["progress"] as Long
                    todoList!!.add(todoItem)
                    println("todoList: $todoList")
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun addNewListDialog() {

        val alert = AlertDialog.Builder(this)
        val newListTitle = EditText(this)

        alert.setTitle("Add new to do list")
        alert.setMessage("Enter list name")
        alert.setView(newListTitle)

        alert.setPositiveButton("Save") { dialog, _ ->
            val taskList = TaskList.create()
            // Using push() method, we obtain a new ID from firebase which we can provide to our To Do item.
            val newListKey = firebaseDatabase.child(Constants.FIREBASE_LIST).push()

            taskList.objectId = newListKey.key
            taskList.listTitle = newListTitle.text.toString()
            taskList.progress = 0
            // Once we do setValue(…), the todoItem will be saved in firebase database.
            newListKey.setValue(taskList)

            val id = taskList.objectId.toString()
            val todoList = TaskList(id, newListTitle.text.toString(), 0)
            adapter.addNewList(todoList)
            dialog.dismiss()
        }
        alert.show()
    }

    object Constants {
        @JvmStatic val FIREBASE_LIST: String = "To do lists"
    }
}