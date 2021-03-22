package com.example.todoapp

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.tasks.AllTasksAdapter
import com.example.todoapp.tasks.data.TaskList
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: AllTasksAdapter

    private lateinit var reference: DatabaseReference
    private var firebaseDatabase = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskAdapter = AllTasksAdapter(mutableListOf())

        allTasksRecyclerView.adapter = taskAdapter
        allTasksRecyclerView.layoutManager = LinearLayoutManager(this)

        buttonAddNewTaskList.setOnClickListener {
            addNewListDialog()
        }

        firebaseDatabase.child("hei").setValue("test")
            }

    private fun addNewListDialog() {
        val alert = AlertDialog.Builder(this)
        val newListTitle = EditText(this)

        alert.setTitle("Add new to do list")
        alert.setMessage("Enter list name")
        alert.setView(newListTitle)

        alert.setPositiveButton("Save") { dialog, positiveButton ->
            val listTitle = newListTitle.text.toString()
            val todo = TaskList(listTitle, 0)
            taskAdapter.addNewList(todo)
        }
        alert.show()
    }
}