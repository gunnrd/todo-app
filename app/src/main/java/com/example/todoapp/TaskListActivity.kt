package com.example.todoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.tasks.TaskItemsAdapter
import com.example.todoapp.tasks.data.TaskItem
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : AppCompatActivity(){

    private lateinit var itemAdapter: TaskItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        editTextTaskText.isVisible = false
        buttonAddNewItem.isVisible = false
        buttonDeleteCheckedItems.isVisible = false

        itemAdapter = TaskItemsAdapter(mutableListOf())

        taskItemsRecyclerView.adapter = itemAdapter
        taskItemsRecyclerView.layoutManager = LinearLayoutManager(this)

        buttonAddNewTask.setOnClickListener {
            editTextTaskText.isVisible = true
            buttonAddNewItem.isVisible = true
        }

        buttonAddNewItem.setOnClickListener {
            val itemText = editTextTaskText.text.toString()
            if(itemText.isNotEmpty()) {
                val newText = TaskItem(itemText)
                itemAdapter.addNewItem(newText)
                editTextTaskText.text.clear()
                editTextTaskText.isVisible = false
                buttonAddNewItem.isVisible = false
            }
        }
    }

}