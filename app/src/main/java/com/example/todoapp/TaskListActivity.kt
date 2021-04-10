package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.tasks.TaskListAdapter
import com.example.todoapp.tasks.data.TaskList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : AppCompatActivity() {

    private lateinit var reference: DatabaseReference
    private var database = FirebaseDatabase.getInstance().reference
    private var currentUser = FirebaseAuth.getInstance().currentUser

    private lateinit var recyclerView: RecyclerView
    private var taskList: MutableList<TaskList>? = null

    private lateinit var eventListenerGetDataFromFirebase: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        setSupportActionBar(taskListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        database.keepSynced(true)

        reference = database.child(currentUser!!.uid).child("To do lists")

        taskList = mutableListOf()

        recyclerView = findViewById(R.id.taskListRecyclerView)
        recyclerView.adapter = TaskListAdapter(taskList!!, this::deleteCardListOnClick)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getDataFromFirebase()
        resetFAButtons()
        buttonViewHandler()

        buttonMyProfile.setOnClickListener {
            startActivity(Intent(this, MyProfileActivity::class.java))
        }

        buttonChangeTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            buttonChangeTheme.setOnClickListener {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }
        }

        val buttonAddNewList = findViewById<View>(R.id.buttonAddNewTaskList) as FloatingActionButton
        buttonAddNewList.setOnClickListener {
            addNewListDialog()
        }

        buttonDeleteAllLists.setOnClickListener {
            deleteAllLists()
        }
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(eventListenerGetDataFromFirebase)
    }

    override fun onResume() {
        super.onResume()
        getDataFromFirebase()
    }

    override fun onDestroy() {
        super.onDestroy()
        reference.removeEventListener(eventListenerGetDataFromFirebase)
    }

    private fun getDataFromFirebase() {

        eventListenerGetDataFromFirebase = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val allLists = taskList
                allLists?.clear()

                for (data in snapshot.children) {
                    val list = data.getValue(TaskList::class.java)

                    if (list != null) {
                        allLists?.add(list)
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MainActivity", "loadItem:onCancelled database error", error.toException())
            }
        }

        eventListenerGetDataFromFirebase = reference.addValueEventListener(eventListenerGetDataFromFirebase)
    }

    private fun addNewListDialog() {
        val alert = AlertDialog.Builder(this)
        val editTextAddListTitle = EditText(this)

        alert.setTitle("Add new list")
        alert.setMessage("Enter list name")
        alert.setView(editTextAddListTitle)

        alert.setPositiveButton("Save") { _, _ ->
            val newListTitle = editTextAddListTitle.text.toString().trim()
            val taskList = TaskList(newListTitle, 0, 0)
            reference.push().key

            if (!invalidCharactersFirebase(newListTitle)) {
                reference.child(newListTitle).setValue(taskList)
                resetFAButtons()
            }
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            resetFAButtons()
        }

        alert.show()
    }

    private fun deleteCardListOnClick(taskList: TaskList) {
        val alert = AlertDialog.Builder(this)

        alert.setTitle("Delete list")
        alert.setMessage("This will delete the list permanently!")

        alert.setPositiveButton("Delete") { _, _ ->
            taskList.listTitle?.let {
                reference.child(it).removeValue()
            }
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

    private fun deleteAllLists() {
        val alert = AlertDialog.Builder(this)

        alert.setTitle("Delete all lists")
        alert.setMessage("Warning! This will delete ALL lists permanently.")

        alert.setPositiveButton("Delete") { _, _ ->
            reference.removeValue()
            resetFAButtons()
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            resetFAButtons()
        }

        alert.show()
    }

    private fun resetFAButtons() {
        buttonExtendedTaskList.shrink()
        buttonDeleteAllLists.hide()
        buttonAddNewTaskList.hide()
    }

    private fun buttonViewHandler() {
        var isVisible = false

        buttonExtendedTaskList.setOnClickListener {
            isVisible = if (!isVisible) {
                buttonDeleteAllLists.show()
                buttonAddNewTaskList.show()
                buttonExtendedTaskList.extend()
                true
            } else {
                resetFAButtons()
                false
            }
        }
    }

    private fun invalidCharactersFirebase(listName: String): Boolean {
        val check: Boolean

        when {
            listName.isEmpty() -> {
                Toast.makeText(this, "List name is required", Toast.LENGTH_SHORT).show()
                check = true
            }
            listName.contains('.') -> {
                Toast.makeText(this, "List name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            listName.contains('#') -> {
                Toast.makeText(this, "List name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            listName.contains('$') -> {
                Toast.makeText(this, "List name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            listName.contains('`') -> {
                Toast.makeText(this, "List name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            listName.contains('´') -> {
                Toast.makeText(this, "List name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            listName.contains('[') -> {
                Toast.makeText(this, "List name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            listName.contains(']') -> {
                Toast.makeText(this, "List name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            listName.contains('/') -> {
                Toast.makeText(this, "List name contains invalid characters.\n .#$`´[]/ is not allowed.", Toast.LENGTH_LONG).show()
                check = true
            }
            else -> {
                check = false
            }
        }

        return check
    }
}