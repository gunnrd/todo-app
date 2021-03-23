package com.example.todoapp.tasks

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.TaskListActivity
import com.example.todoapp.databinding.AllTasksLayoutBinding
import com.example.todoapp.tasks.data.TaskList
import kotlinx.android.synthetic.main.all_tasks_layout.view.*

class AllTasksAdapter (context: Context, private val taskList:MutableList<TaskList>) : RecyclerView.Adapter<AllTasksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AllTasksLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskName = taskList[position]
        holder.itemView.apply {
            cardListName.text = taskName.listTitle
        }
    }

    inner class ViewHolder(private val binding: AllTasksLayoutBinding): RecyclerView.ViewHolder(binding.root) {
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

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    fun addNewList(taskList: TaskList) {
        this.taskList.add(taskList)
        notifyItemInserted(this.taskList.size - 1)
        notifyDataSetChanged()
    }
    /*
    fun deleteList(position: Int) {
        //TODO Get adapter position
        taskList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }*/
}
