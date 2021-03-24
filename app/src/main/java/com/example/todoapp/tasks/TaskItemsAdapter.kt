package com.example.todoapp.tasks

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.todoapp.databinding.TaskItemsLayoutBinding
import com.example.todoapp.tasks.data.TaskItems
import kotlinx.android.synthetic.main.task_items_layout.view.*

class TaskItemsAdapter(private val taskItems:MutableList<TaskItems>) : RecyclerView.Adapter<TaskItemsAdapter.ViewHolder>() {

    private var counterCheckedItems: Float = 0.0F
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TaskItemsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(taskItems[position])


        holder.itemView.apply {
            val task = taskItems[position]
            taskText.text = task.taskName
            checkBoxTaskDone.isChecked = task.isDone
            strikeThroughItem(taskText, task.isDone)

            checkBoxTaskDone.setOnCheckedChangeListener { _, isChecked ->
                strikeThroughItem(taskText, isChecked)
                task.isDone = !task.isDone

                if (checkBoxTaskDone.isChecked) {
                    counterCheckedItems += 1
                    println("counterCheckedItems: $counterCheckedItems")
                    calculateProgress()
                } else {
                    counterCheckedItems -= 1
                    println("counterCheckedItems: $counterCheckedItems")
                    calculateProgress()
                }
            }
        }
    }

    class ViewHolder(private val binding: TaskItemsLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(taskitems: TaskItems) {

            binding.checkBoxTaskDone.isChecked = taskitems.isDone
            binding.taskText.text = taskitems.taskName
        }
    }

    override fun getItemCount(): Int = taskItems.size

    private fun calculateProgress(): Int {
        val totalProgressBar = taskItems.size
        println("totalProgressBar: $totalProgressBar")
        val progressValue = (counterCheckedItems/totalProgressBar) * 100
        println("progressValue: $progressValue")

        return progressValue.toInt()
    }

    private fun strikeThroughItem(itemText: TextView, isChecked: Boolean) {
        if(isChecked) {
            itemText.paintFlags = itemText.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            itemText.paintFlags = itemText.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}