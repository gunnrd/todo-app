package com.example.todoapp

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.tasks.TaskItemsAdapter
import com.example.todoapp.tasks.data.TaskItems
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : AppCompatActivity(){

    private lateinit var reference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private var taskItems: MutableList<TaskItems>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        setSupportActionBar(taskItemsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        reference = FirebaseDatabase.getInstance().getReference("To do lists")

        taskItems = mutableListOf()

        recyclerView = findViewById(R.id.taskItemsRecyclerView)
        recyclerView.adapter = TaskItemsAdapter(taskItems!!, this::saveCheckboxStatus)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getDataFromFirebase()

        buttonAddNewTask.setOnClickListener {
            addNewItemDialog()
        }

        listTitle.text = intent.getStringExtra("TITLE")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun saveCheckboxStatus(item: TaskItems) {

        val listId = listTitle.text.toString()

        if (item.done) {
            item.taskName?.let {
                reference.child(listId).child("/listItems").child(it).child("done").setValue(false)
            }
        } else {
            item.taskName?.let {
                reference.child(listId).child("/listItems").child(it).child("done").setValue(true)
            }
        }
    }

    private fun getDataFromFirebase() {
        val listId = intent.getStringExtra("TITLE")
        reference.child(listId.toString()).child("listItems").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val allItems = taskItems
                val adapter = recyclerView.adapter
                allItems?.clear()

                for (data in snapshot.children) {
                    val items = data.getValue(TaskItems::class.java)
                    recyclerView.adapter = adapter
                    if (items != null) {
                        allItems?.add(items)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TaskListActivity", "loadItem:onCancelled database error", error.toException())
            }
        })
    }

    private fun addNewItemDialog() {
        val alert = AlertDialog.Builder(this)
        val editTextItemText = EditText(this)

        alert.setTitle("Add new item")
        alert.setMessage("Enter task name")
        alert.setView(editTextItemText)

        alert.setPositiveButton("Save") { dialog, _ ->
            val newListItemText = editTextItemText.text.toString().trim()
            //val newItem = TaskItems(newListItemText, false)
            val newItem = TaskItems(newListItemText)
            val listId = listTitle.text.toString()

            reference.child(listId).child("listItems").child(newListItemText).setValue(newItem)
            dialog.dismiss()
        }
        alert.show()
    }
}