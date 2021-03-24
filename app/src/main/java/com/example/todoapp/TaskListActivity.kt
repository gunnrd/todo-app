package com.example.todoapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.tasks.TaskItemsAdapter
import com.example.todoapp.tasks.data.TaskItems
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : AppCompatActivity(){

    private lateinit var itemAdapter: TaskItemsAdapter
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        setSupportActionBar(taskItemsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        reference = FirebaseDatabase.getInstance().getReference("To do lists")

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
        val editTextItemText = EditText(this)

        alert.setTitle("Add new item")
        alert.setMessage("Enter task name")
        alert.setView(editTextItemText)

        alert.setPositiveButton("Save") { dialog, _ ->
            val newListItemText = editTextItemText.text.toString().trim()
            val newItem = TaskItems(newListItemText, 0, false)
            val listId = listTitle.text.toString()

            reference.child(listId).child("To do items").child(newListItemText).setValue(newItem)
            itemAdapter.addNewItem(newItem)
            itemAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        alert.show()
    }
}