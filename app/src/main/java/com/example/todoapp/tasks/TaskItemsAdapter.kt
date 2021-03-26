package com.example.todoapp.tasks

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.todoapp.databinding.TaskItemsLayoutBinding
import com.example.todoapp.tasks.data.TaskItems

class TaskItemsAdapter(private val taskItems:MutableList<TaskItems>, private val clickCheckbox: (TaskItems) -> Unit) : RecyclerView.Adapter<TaskItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TaskItemsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(taskItems[position], clickCheckbox)
    }

    class ViewHolder(private val binding: TaskItemsLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(taskItems: TaskItems, clickCheckbox: (TaskItems) -> Unit) {

            binding.checkBoxTask.isChecked = taskItems.done
            binding.taskText.text = taskItems.taskName

            binding.checkBoxTask.setOnClickListener {
                clickCheckbox(taskItems)
            }
        }
    }

    override fun getItemCount(): Int = taskItems.size
}