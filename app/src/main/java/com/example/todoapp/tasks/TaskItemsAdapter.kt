package com.example.todoapp.tasks

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todoapp.R
import com.example.todoapp.databinding.TaskItemsLayoutBinding
import com.example.todoapp.tasks.data.TaskItem
import kotlinx.android.synthetic.main.task_items_layout.view.*

class TaskItemsAdapter(private val taskItems:MutableList<TaskItem>) : RecyclerView.Adapter<TaskItemsAdapter.ViewHolder>() {

    class ViewHolder(itemsView: View): RecyclerView.ViewHolder(itemsView)

    override fun getItemCount(): Int = taskItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskItems[position]
        holder.itemView.apply {
            taskText.text = task.taskName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_items_layout, parent, false))
    }

    fun addNewItem(taskItem: TaskItem) {
        taskItems.add(taskItem)
        notifyItemInserted(taskItems.size - 1)
    }


}