package com.example.todoapp.tasks

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.TaskListActivity
import com.example.todoapp.databinding.AllTasksLayoutBinding
import com.example.todoapp.databinding.TaskItemsLayoutBinding
import com.example.todoapp.tasks.data.TaskItems
import com.example.todoapp.tasks.data.TaskList
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.all_tasks_layout.view.*
import kotlin.reflect.KFunction0

class AllTasksAdapter(private val taskList:MutableList<TaskList>, private val deleteListClick: (TaskList) -> Unit) : RecyclerView.Adapter<AllTasksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AllTasksLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taskList[position], deleteListClick)
    }

    inner class ViewHolder(private val binding: AllTasksLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(list: TaskList, deleteListClick: (TaskList) -> Unit) {
            binding.cardListName.text = list.listTitle
            binding.cardProgressBar.progress = list.progress!!

            binding.cardDeleteButton.setOnClickListener {
                deleteListClick(list)
            }
        }

        private val context = binding.root.context
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

    override fun getItemCount(): Int = taskList.size
}
