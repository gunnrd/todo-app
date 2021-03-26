package com.example.todoapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.tasks.AllTasksAdapter
import com.example.todoapp.tasks.data.TaskList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var reference: DatabaseReference
    private var database = FirebaseDatabase.getInstance().reference

    private lateinit var recyclerView: RecyclerView
    private var taskList: MutableList<TaskList>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reference = database.child("To do lists")

        taskList = mutableListOf()

        recyclerView = findViewById(R.id.allTasksRecyclerView)
        recyclerView.adapter = AllTasksAdapter(taskList!!, this::deleteListClick)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getDataFromFirebase()

        val buttonAddNewList = findViewById<View>(R.id.buttonAddNewTaskList) as FloatingActionButton

        buttonAddNewList.setOnClickListener {
            addNewListDialog()
        }
    }

    private fun deleteListClick(taskList: TaskList) {
        taskList.listTitle?.let { reference.child(it).removeValue() }
    }

    private fun getDataFromFirebase() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allLists = taskList
                val adapter = recyclerView.adapter
                allLists?.clear()

                for (data in snapshot.children) {
                    val list = data.getValue(TaskList::class.java)
                    recyclerView.adapter = adapter

                    if (list != null) {
                        allLists?.add(list)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MainActivity", "loadItem:onCancelled database error", error.toException())
            }
        })
    }

    private fun addNewListDialog() {

        val alert = AlertDialog.Builder(this)
        val editTextListTitle = EditText(this)

        alert.setTitle("Add new to do list")
        alert.setMessage("Enter list name")
        alert.setView(editTextListTitle)

        alert.setPositiveButton("Save") { dialog, _ ->
            val newListTitle = editTextListTitle.text.toString().trim()
            val taskList = TaskList(newListTitle, 0)
            // For å sende inn begge verdiene fra data class tasklist
            //val taskList = TaskList(newListTitle, 0)

            val listId = reference.push().key

            when {
                newListTitle.isEmpty() ->
                    Toast.makeText(this, "Enter list title", Toast.LENGTH_SHORT).show()
                listId == null ->
                    Toast.makeText(this, "Error saving list. List id is null", Toast.LENGTH_SHORT).show()
                listId.contentEquals(newListTitle) ->
                    Toast.makeText(this, "List name already exists. Enter another name.", Toast.LENGTH_SHORT).show()
                else -> {
                 reference.child(newListTitle).setValue(taskList)
                }
            }
            dialog.dismiss()
        }
        alert.show()
    }
}