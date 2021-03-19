package com.example.todoapp.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.tasks.data.TaskListName
import kotlinx.android.synthetic.main.all_tasks_layout.view.*

class AllTasksAdapter (private val taskNames:MutableList<TaskListName>) : RecyclerView.Adapter<AllTasksAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.all_tasks_layout, parent, false))
    }

    fun addNewList(taskList: TaskListName) {
        taskNames.add(taskList)
        notifyItemInserted(taskNames.size - 1)
        notifyDataSetChanged()
    }

    fun deleteList() {
        //TODO Add delete list function
    }

    override fun getItemCount(): Int = taskNames.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskName = taskNames[position]
        holder.itemView.apply {
            cardListName.text = taskName.taskListName
        }
    }
}