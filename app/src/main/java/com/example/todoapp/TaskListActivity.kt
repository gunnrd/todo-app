package com.example.todoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.tasks.TaskItemsAdapter
import com.example.todoapp.tasks.data.TaskItem
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : AppCompatActivity(){

    private lateinit var itemsAdapter: TaskItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        itemsAdapter = TaskItemsAdapter(mutableListOf())

        taskItemsRecyclerView.adapter.itemsAdapter
        taskItemsRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}