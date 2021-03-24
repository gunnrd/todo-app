package com.example.todoapp.tasks.data

data class TaskList(var listTitle:String? = null)

data class TaskItems(var taskName:String? = null, var progress:Int? = null, var isDone: Boolean = false)