package com.example.todoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.tasks.AllTasksAdapter
import com.example.todoapp.tasks.data.TaskListName
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: AllTasksAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextAddNewList.isVisible = false
        buttonAddNewList.isVisible = false

        taskAdapter = AllTasksAdapter(mutableListOf())

        allTasksRecyclerView.adapter = taskAdapter
        allTasksRecyclerView.layoutManager = LinearLayoutManager(this)

        buttonAddNewTaskList.setOnClickListener {
            editTextAddNewList.isVisible = true
            buttonAddNewList.isVisible = true
        }

        buttonAddNewList.setOnClickListener {
            val listTitle = editTextAddNewList.text.toString()
            if(listTitle.isNotEmpty()) {
                val todo = TaskListName(listTitle, 0)
                taskAdapter.addNewList(todo)
                editTextAddNewList.text.clear()
                editTextAddNewList.isVisible = false
                buttonAddNewList.isVisible = false
            }
        }
    }
}