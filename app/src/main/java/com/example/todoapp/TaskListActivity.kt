package com.example.todoapp

import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.tasks.TaskItemsAdapter
import com.example.todoapp.tasks.data.TaskItems
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : AppCompatActivity(){

    private lateinit var itemAdapter: TaskItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        setSupportActionBar(taskItemsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        itemAdapter = TaskItemsAdapter(mutableListOf())

        taskItemsRecyclerView.adapter = itemAdapter
        taskItemsRecyclerView.layoutManager = LinearLayoutManager(this)

        buttonAddNewTask.setOnClickListener {
            addNewItemDialog()
        }

        listTitle.text = intent.getStringExtra("TITLE")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun progress() {
        //TODO Set progress value to progressbars
        val progressBarItems = findViewById<ProgressBar>(R.id.itemsProgressBar)
        val progressBarList = findViewById<ProgressBar>(R.id.cardProgressBar)
        progressBarItems.progress = itemAdapter.calculateProgress()
        progressBarList.progress = itemAdapter.calculateProgress()
    }
    
    private fun addNewItemDialog() {
        val alert = AlertDialog.Builder(this)
        val newItem = EditText(this)

        alert.setTitle("Add new item")
        alert.setMessage("Enter task name")
        alert.setView(newItem)

        alert.setPositiveButton("Save") { _, _ ->
            val listItem = newItem.text.toString()
            val item = TaskItems(listItem, 0, false)
            itemAdapter.addNewItem(item)
        }
        alert.show()
    }
}