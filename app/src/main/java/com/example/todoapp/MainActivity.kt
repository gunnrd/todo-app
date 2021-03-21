package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.tasks.AllTasksAdapter
import com.example.todoapp.tasks.data.TaskListName
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.all_tasks_layout.*

const val TITLE = "com.example.todoapp.TITLE"

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: AllTasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskAdapter = AllTasksAdapter(mutableListOf())

        allTasksRecyclerView.adapter = taskAdapter
        allTasksRecyclerView.layoutManager = LinearLayoutManager(this)

        buttonAddNewTaskList.setOnClickListener {
            addNewListDialog()
        }
    }

    fun changeActivity(view: View) {
        val titleHeader = findViewById<TextView>(R.id.cardListName)
        val title = titleHeader.text.toString()
        val intent = Intent(this, TaskListActivity::class.java).apply {
            putExtra(TITLE, title)
        }
        startActivity(intent)
    }

    private fun addNewListDialog() {
        val alert = AlertDialog.Builder(this)
        val newListTitle = EditText(this)

        alert.setTitle("Add new to do list")
        alert.setMessage("Enter list name")
        alert.setView(newListTitle)

        alert.setPositiveButton("Save") { dialog, positiveButton ->
            val listTitle = newListTitle.text.toString()
            val todo = TaskListName(listTitle, 0)
            taskAdapter.addNewList(todo)
        }
        alert.show()
    }
}