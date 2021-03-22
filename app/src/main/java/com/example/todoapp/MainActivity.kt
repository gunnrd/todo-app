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

lateinit var firebaseDatabase: DatabaseReference

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: AllTasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseDatabase = FirebaseDatabase.getInstance().reference

        taskAdapter = AllTasksAdapter(mutableListOf())

        allTasksRecyclerView.adapter = taskAdapter
        allTasksRecyclerView.layoutManager = LinearLayoutManager(this)

        buttonAddNewTaskList.setOnClickListener {
            addNewListDialog()
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
            val newList = firebaseDatabase.child(Constants.FIREBASE_LIST).push()

            taskList.objectId = newList.key
            taskList.listTitle = newListTitle.text.toString()
            taskList.progress = 0
            newList.setValue(taskList)

            val id = taskList.objectId.toString()
            val todoList = TaskList(id, newListTitle.text.toString(), 0)
            taskAdapter.addNewList(todoList)
            dialog.dismiss()
        }
        alert.show()
    }

    object Constants {
        @JvmStatic val FIREBASE_LIST: String = "To do lists"
    }
}