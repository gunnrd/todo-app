package com.example.todoapp.tasks

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.TaskListActivity
import com.example.todoapp.databinding.AllTasksLayoutBinding
import com.example.todoapp.tasks.data.TaskList
import kotlinx.android.synthetic.main.all_tasks_layout.view.*

class AllTasksAdapter (context: Context, private val taskList:MutableList<TaskList>) : RecyclerView.Adapter<AllTasksAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var todoList = taskList

    fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val listId: String = todoList.get(position).objectId as String
        val listTitle: String = todoList.get(position).listTitle as String
        val progress: Int = todoList.get(position).progress as Int

        val view: View
        val viewHolder: CardHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.all_tasks_layout, parent, false)
            viewHolder = CardHolder(view)
            view.tag = viewHolder
            } else {
            view = convertView
            viewHolder = view.tag as CardHolder
        }

        viewHolder.title.text = listTitle
        viewHolder.progress.progress = 0

        return view
    }

    fun getItem(index: Int): Any {
        return todoList[index]
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    private class CardHolder(card: View?) {
        val title: TextView = card!!.findViewById<TextView>(R.id.cardListName) as TextView
        val progress: ProgressBar = card!!.findViewById<ProgressBar>(R.id.cardProgressBar) as ProgressBar
        val delete: ImageButton = card!!.findViewById<ImageButton>(R.id.cardDeleteButton) as ImageButton
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AllTasksLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskName = taskList[position]
        holder.itemView.apply {
            cardListName.text = taskName.listTitle
        }
    }

    fun addNewList(taskList: TaskList) {
        this.taskList.add(taskList)
        notifyItemInserted(this.taskList.size - 1)
        notifyDataSetChanged()
    }
    /*
    fun deleteList(index: Int) {
        //TODO Get index
        taskList.removeAt(index)
        notifyItemRemoved(index)
        notifyDataSetChanged()
    }*/

}
