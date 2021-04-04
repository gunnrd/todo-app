package com.example.todoapp.tasks

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.TaskItemsActivity
import com.example.todoapp.databinding.TaskListLayoutBinding
import com.example.todoapp.tasks.data.TaskList
import kotlinx.android.synthetic.main.task_list_layout.view.*

class TaskListAdapter(private val taskList: MutableList<TaskList>, private val deleteListClick: (TaskList) -> Unit) : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TaskListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taskList[position], deleteListClick)
    }

    inner class ViewHolder(private val binding: TaskListLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(list: TaskList, deleteListClick: (TaskList) -> Unit) {
            binding.cardListName.text = list.listTitle
            binding.cardProgressBar.max = list.itemCount!!
            binding.cardProgressBar.progress = list.progress!!

            binding.cardDeleteButton.setOnClickListener {
                deleteListClick(list)
            }
        }

        private val context = binding.root.context
        init {
            itemView.setOnClickListener {
                val titleHeader = binding.root.cardListName
                val intent = Intent(context, TaskItemsActivity::class.java).apply {
                    putExtra("TITLE", titleHeader.text)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = taskList.size
}
