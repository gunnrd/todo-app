package com.example.todoapp.tasks

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.TaskListActivity
import com.example.todoapp.databinding.AllTasksLayoutBinding
import com.example.todoapp.tasks.data.TaskList
import kotlinx.android.synthetic.main.all_tasks_layout.view.*

class AllTasksAdapter (private val tasks:MutableList<TaskList>) : RecyclerView.Adapter<AllTasksAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: AllTasksLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val context = binding.root.context
        init {

            itemView.setOnClickListener {
                val titleHeader = binding.root.cardListName
                val intent = Intent(context, TaskListActivity::class.java).apply {
                    putExtra("TITLE", titleHeader.text)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AllTasksLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskName = tasks[position]
        holder.itemView.apply {
            cardListName.text = taskName.taskListName
        }

    }

    fun addNewList(taskList: TaskList) {
        tasks.add(taskList)
        notifyItemInserted(tasks.size - 1)
        notifyDataSetChanged()
    }

    fun deleteList(index: Int) {
        //TODO Get index
        tasks.removeAt(index)
        notifyItemRemoved(index)
        notifyDataSetChanged()
    }

}