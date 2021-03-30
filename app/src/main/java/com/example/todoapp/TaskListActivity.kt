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
    private var auth = FirebaseAuth.getInstance()

    private lateinit var recyclerView: RecyclerView
    private var taskList: MutableList<TaskList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        setSupportActionBar(taskListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val currentUser = auth.currentUser
        reference = database.child(currentUser!!.uid).child("To do lists")

        taskList = mutableListOf()

        recyclerView = findViewById(R.id.taskListRecyclerView)
        recyclerView.adapter = TaskListAdapter(taskList!!, this::deleteCardListOnClick)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getDataFromFirebase()

        val buttonAddNewList = findViewById<View>(R.id.buttonAddNewTaskList) as FloatingActionButton
        buttonAddNewList.setOnClickListener {
            addNewListDialog()
        }

        buttonChangeTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            buttonChangeTheme.setOnClickListener {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }
        }

        buttonLogOut.setOnClickListener {
            logOut()
        }

        /*
        buttonMyProfile.setOnClickListener {
            //TODO
        }*/
    }

    private fun deleteCardListOnClick(taskList: TaskList) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Delete list")
        alert.setMessage("This will delete the list permanently!")
        alert.setPositiveButton("Delete") { _, _ ->
            taskList.listTitle?.let { reference.child(it).removeValue() }
        }
        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alert.show()
    }

    private fun getDataFromFirebase() {
        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                recyclerView.adapter?.notifyDataSetChanged()
                val allLists = taskList
                val adapter = recyclerView.adapter
                allLists?.clear()

                for (data in snapshot.children) {
                    val list = data.getValue(TaskList::class.java)
                    recyclerView.adapter = adapter

                    if (list != null) {
                        allLists?.add(list)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MainActivity", "loadItem:onCancelled database error", error.toException())
            }
        })
    }

    private fun addNewListDialog() {
        val alert = AlertDialog.Builder(this)
        val editTextListTitle = EditText(this)

        alert.setTitle("Add new list")
        alert.setMessage("Enter list name")
        alert.setView(editTextListTitle)

        alert.setPositiveButton("Save") { _, _ ->

            val newListTitle = editTextListTitle.text.toString().trim()
            val taskList = TaskList(newListTitle, 0)
            val listId = reference.push().key

            when {
                newListTitle.isEmpty() ->
                    Toast.makeText(this, "Enter list title", Toast.LENGTH_SHORT).show()
                listId == null ->
                    Toast.makeText(this, "Error saving list. List id is null", Toast.LENGTH_SHORT).show()
                listId.contentEquals(newListTitle) ->
                    Toast.makeText(
                        this,
                        "List name already exists. Enter another name.",
                        Toast.LENGTH_SHORT
                    ).show()
                else -> {
                    reference.child(newListTitle).setValue(taskList)
                }
            }
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alert.create()
        alert.show()
    }

    private fun logOut() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Log out?")

        alert.setPositiveButton("Ok") { _, _ ->
            auth.signOut()

            if (auth.currentUser == null) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alert.show()
    }
}