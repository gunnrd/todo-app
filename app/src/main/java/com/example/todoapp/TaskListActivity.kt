package com.example.todoapp

import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.tasks.TaskItemsAdapter
import com.example.todoapp.tasks.data.TaskItem
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : AppCompatActivity(){

    private lateinit var itemAdapter: TaskItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        itemAdapter = TaskItemsAdapter(mutableListOf())

        taskItemsRecyclerView.adapter = itemAdapter
        taskItemsRecyclerView.layoutManager = LinearLayoutManager(this)

        buttonAddNewTask.setOnClickListener {
            addNewItemDialog()
        }

        val title = intent.getStringExtra(TITLE)
        val titleHeader = findViewById<TextView>(R.id.listTitle).apply {
            text = title
        }
    }

    private fun progress() {
        //TODO Set progress to progressbar
        var progressBar = findViewById<ProgressBar>(R.id.itemsProgressBar)

    }

    private fun addNewItemDialog() {
        val alert = AlertDialog.Builder(this)
        val newItem = EditText(this)

        alert.setTitle("Add new item")
        alert.setMessage("Enter task name")
        alert.setView(newItem)

        alert.setPositiveButton("Save") { dialog, positiveButton ->
            val listItem = newItem.text.toString()
            val item = TaskItem(listItem, false)
            itemAdapter.addNewItem(item)
        }
        alert.show()
    }
}