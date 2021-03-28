package com.example.todoapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.tasks.AllTasksAdapter
import com.example.todoapp.tasks.data.TaskList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

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
        recyclerView.adapter = AllTasksAdapter(taskList!!, this::deleteCardListOnClick)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getDataFromFirebase()

        val buttonAddNewList = findViewById<View>(R.id.buttonAddNewTaskList) as FloatingActionButton
        buttonAddNewList.setOnClickListener {
            addNewListDialog()
        }

        buttonChangeTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            buttonChangeTheme.setOnClickListener {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }
        }
    }

    private fun deleteCardListOnClick(taskList: TaskList) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Delete list")
        alert.setMessage("This will delete the list permanently!")
        alert.setPositiveButton("Delete") { _, _ ->
            taskList.listTitle?.let { reference.child(it).removeValue() }
        }
        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alert.show()
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

        alert.setTitle("Add new list")
        alert.setMessage("Enter list name")
        alert.setView(editTextListTitle)

        alert.setPositiveButton("Save") { _, _ ->

            val newListTitle = editTextListTitle.text.toString().trim()
            val taskList = TaskList(newListTitle, 0)
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
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }
}