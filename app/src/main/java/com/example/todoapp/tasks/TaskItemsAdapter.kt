package com.example.todoapp.tasks

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.todoapp.R
import com.example.todoapp.tasks.data.TaskItem
import kotlinx.android.synthetic.main.task_items_layout.view.*

class TaskItemsAdapter(private val taskItems:MutableList<TaskItem>) : RecyclerView.Adapter<TaskItemsAdapter.ViewHolder>() {

    class ViewHolder(itemsView: View): RecyclerView.ViewHolder(itemsView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.task_items_layout, parent, false))
    }

    override fun getItemCount(): Int = taskItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskItems[position]

        holder.itemView.apply {
            taskText.text = task.taskName
            checkBoxTaskDone.isChecked = task.isChecked
            strikeThroughItem(taskText, task.isChecked)
            checkBoxTaskDone.setOnCheckedChangeListener { _, isChecked ->
                strikeThroughItem(taskText, isChecked)
                task.isChecked = !task.isChecked
            }
        }
    }

    fun addNewItem(taskItem: TaskItem) {
        taskItems.add(taskItem)
        notifyItemInserted(taskItems.size - 1)
        notifyDataSetChanged()
    }

    fun deleteItems() {
        taskItems.removeAll {
            taskItem ->  taskItem.isChecked
        }
        notifyDataSetChanged()
    }

    private fun setProgress() {
        val totalProgress = taskItems.size
        var progressValue = 100 / totalProgress
    }

    private fun strikeThroughItem(itemText: TextView, isChecked: Boolean) {
        if(isChecked) {
            itemText.paintFlags = itemText.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            itemText.paintFlags = itemText.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }


}