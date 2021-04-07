package com.example.todoapp

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
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
    private var database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()

    private lateinit var recyclerView: RecyclerView
    private var taskItems: MutableList<TaskItems>? = null

    private lateinit var eventListenerProgressBar: ValueEventListener
    private lateinit var eventListenerGetTaskItemCount: ValueEventListener
    private lateinit var eventListenerGetDataFromFirebase: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_items)

        setSupportActionBar(taskItemsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        listTitle.text = intent.getStringExtra("TITLE")

        val currentUser = auth.currentUser
        reference = database.child(currentUser!!.uid).child("To do lists")

        taskItems = mutableListOf()

        recyclerView = findViewById(R.id.taskItemsRecyclerView)
        recyclerView.adapter = TaskItemsAdapter(taskItems!!, this::saveCheckboxStatus, this::deleteItem)
        recyclerView.layoutManager = LinearLayoutManager(this)

        resetFAButtons()
        buttonViewHandler()

        buttonAddNewItem.setOnClickListener {
            addNewItemDialog()
        }

        buttonDeleteAllItems.setOnClickListener {
            deleteAllItems()
        }
    }

    override fun onStart() {
        super.onStart()
        getDataFromFirebase()
        getTaskItemCount()
        saveProgressBarStatus()
    }

    override fun onStop() {
        super.onStop()
        cleanUpEventListeners()
    }

    private fun cleanUpEventListeners() {
        reference.removeEventListener(eventListenerProgressBar)
        reference.removeEventListener(eventListenerGetTaskItemCount)
        reference.removeEventListener(eventListenerGetDataFromFirebase)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun getDataFromFirebase() {
        val listId = intent.getStringExtra("TITLE")

        eventListenerGetDataFromFirebase = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                recyclerView.adapter?.notifyDataSetChanged()
                val allItems = taskItems
                allItems?.clear()

                for (data in snapshot.children) {
                    val items = data.getValue(TaskItems::class.java)
                    if (items != null) {
                        allItems?.add(items)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TaskListActivity", "loadItem:onCancelled database error", error.toException())
            }
        }

        reference.child(listId.toString()).child("listItems")
            .addValueEventListener(eventListenerGetDataFromFirebase)
    }

    private fun addNewItemDialog() {
        val alert = AlertDialog.Builder(this)
        val editTextItemText = EditText(this)

        alert.setTitle("Add new item")
        alert.setMessage("Enter item text")
        alert.setView(editTextItemText)

        alert.setPositiveButton("Save") { _, _ ->
            val newListItemText = editTextItemText.text.toString().trim()
            val newItem = TaskItems(newListItemText)
            val listId = listTitle.text.toString()

            if (!invalidCharacters(newListItemText)) {
                reference.child(listId).child("listItems").child(newListItemText).setValue(newItem)
            }
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

    private fun getTaskItemCount() {
        val listId = intent.getStringExtra("TITLE").toString()

        eventListenerGetTaskItemCount = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null) {
                    return
                }
                val count = snapshot.childrenCount.toInt()

                progressBarItems.max = count
                reference.child(listId).child("/itemCount").setValue(count)
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        eventListenerGetTaskItemCount = reference.child(listId).child("/listItems")
            .addValueEventListener(eventListenerGetTaskItemCount)
    }

    private fun saveProgressBarStatus() {
        val listId = intent.getStringExtra("TITLE").toString()

        eventListenerProgressBar = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null) {
                    return
                }
                val countCheckedItems = snapshot.childrenCount.toInt()
                reference.child(listId).child("/progress").setValue(countCheckedItems)
                progressBarItems.progress = countCheckedItems
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TaskListActivity", "loadItem:onCancelled database error", error.toException())
            }
        }

        eventListenerProgressBar = reference.child(listId).child("/listItems").orderByChild("done").equalTo(true)
            .addValueEventListener(eventListenerProgressBar)
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

    private fun deleteItem(taskItems: TaskItems) {
        val alert = AlertDialog.Builder(this)

        alert.setTitle("Delete item")
        alert.setMessage("This will delete the item permanently")

        alert.setPositiveButton("Delete") { _, _ ->
            val listId = intent.getStringExtra("TITLE").toString()
            taskItems.taskName?.let { reference.child(listId).child("/listItems").child(it).removeValue() }
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

    private fun deleteAllItems() {
        val alert = AlertDialog.Builder(this)

        alert.setTitle("Delete all items")
        alert.setMessage("Warning! This will delete ALL items permanently.")

        alert.setPositiveButton("Delete") { _, _ ->
            val listId = intent.getStringExtra("TITLE").toString()
            reference.child(listId).child("/listItems").removeValue()
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

    private fun resetFAButtons() {
        buttonDeleteAllItems.hide()
        buttonAddNewItem.hide()
        buttonExtendedTaskItems.shrink()
    }

    private fun buttonViewHandler() {
        var isVisible = false

        buttonExtendedTaskItems.setOnClickListener {
            isVisible = if (!isVisible) {
                buttonDeleteAllItems.show()
                buttonAddNewItem.show()
                buttonExtendedTaskItems.extend()
                true
            } else {
                resetFAButtons()
                false
            }
        }
    }

    private fun invalidCharacters(itemName: String): Boolean {
        val check: Boolean

        when {
            itemName.isEmpty() -> {
                Toast.makeText(this, "Item name is required", Toast.LENGTH_SHORT).show()
                check = true
            }
            itemName.contains('.') -> {
                Toast.makeText(this, "Item name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            itemName.contains('#') -> {
                Toast.makeText(this, "Item name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            itemName.contains('$') -> {
                Toast.makeText(this, "Item name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            itemName.contains('`') -> {
                Toast.makeText(this, "Item name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            itemName.contains('´') -> {
                Toast.makeText(this, "Item name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            itemName.contains('[') -> {
                Toast.makeText(this, "Item name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            itemName.contains(']') -> {
                Toast.makeText(this, "Item name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            itemName.contains('/') -> {
                Toast.makeText(this, "Item name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            else -> {
                check = false
            }
        }

        return check
    }
}