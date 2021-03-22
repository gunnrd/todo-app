package com.example.todoapp.tasks

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.todoapp.R
import com.example.todoapp.tasks.data.TaskItems
import kotlinx.android.synthetic.main.task_items_layout.view.*

class TaskItemsAdapter(private val taskItems:MutableList<TaskItems>) : RecyclerView.Adapter<TaskItemsAdapter.ViewHolder>() {

    class ViewHolder(itemsView: View): RecyclerView.ViewHolder(itemsView)

    private var counterCheckedItems:Float = 0.0F

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

    fun addNewItem(taskItems: TaskItems) {
        this.taskItems.add(taskItems)
        notifyItemInserted(this.taskItems.size - 1)
        notifyDataSetChanged()
    }

    fun deleteItems() {
        taskItems.removeAll {
            taskItem ->  taskItem.isChecked
        }
        notifyDataSetChanged()
    }

    fun calculateProgress(): Int {

        val totalProgressBar = taskItems.size
        println("totalProgressBar: $totalProgressBar")
        val progressValue = (counterCheckedItems/totalProgressBar) * 100
        println("progressValue: $progressValue")

        //notifyDataSetChanged()
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