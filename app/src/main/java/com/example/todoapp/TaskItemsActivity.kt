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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_task_items.*

class TaskItemsActivity : AppCompatActivity(){

    private lateinit var reference: DatabaseReference
    private var auth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance().reference
    private lateinit var recyclerView: RecyclerView
    private var taskItems: MutableList<TaskItems>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_items)

        setSupportActionBar(taskItemsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        listTitle.text = intent.getStringExtra("TITLE")

        val currentUser = auth.currentUser
        reference = database.child(currentUser!!.uid).child("To do lists")

        taskItems = mutableListOf()

        recyclerView = findViewById(R.id.taskItemsRecyclerView)
        recyclerView.adapter = TaskItemsAdapter(taskItems!!, this::saveCheckboxStatus, this::deleteItem)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getDataFromFirebase()
        saveProgressBarStatus()

        buttonAddNewItem.setOnClickListener {
            addNewItemDialog()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun getTaskItemCount() {
        val listId = intent.getStringExtra("TITLE").toString()

        reference.child(listId).child("/listItems").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount.toInt()
                progressBarItems.max = count
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun saveProgressBarStatus() {
        getTaskItemCount()
        val listId = intent.getStringExtra("TITLE").toString()

        reference.child(listId).child("/listItems").orderByChild("done").equalTo(true)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val countCheckedItems = snapshot.childrenCount.toInt()
                    reference.child(listId).child("/progress").setValue(countCheckedItems)
                    progressBarItems.progress = countCheckedItems
                    //TODO Set cardProgressBar.progress = countCheckedItems here?
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("TaskListActivity", "loadItem:onCancelled database error", error.toException())
                }
            })
    }

    private fun deleteItem(taskItems: TaskItems) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Delete item")
        alert.setMessage("This will delete the item permanently!")
        alert.setPositiveButton("Delete") { _, _ ->
            val listId = intent.getStringExtra("TITLE").toString()
            taskItems.taskName?.let { reference.child(listId).child("/listItems").child(it).removeValue() }
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
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
                recyclerView.adapter?.notifyDataSetChanged()
                val allItems = taskItems
                //val adapter = recyclerView.adapter
                allItems?.clear()

                for (data in snapshot.children) {
                    val items = data.getValue(TaskItems::class.java)
                    //recyclerView.adapter = adapter
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

        alert.setPositiveButton("Save") { _, _ ->
            val newListItemText = editTextItemText.text.toString().trim()
            val newItem = TaskItems(newListItemText)
            val listId = listTitle.text.toString()

            reference.child(listId).child("listItems").child(newListItemText).setValue(newItem)
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }
}