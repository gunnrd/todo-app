package com.example.todoapp.tasks.data

data class TaskList(var listTitle:String? = null, var progress:Int? = null)

data class TaskItems(var taskName:String? = null, var done: Boolean = false)