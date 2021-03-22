package com.example.todoapp.tasks.data

data class TaskList(var objectId: String? = null, var listTitle:String? = null, var progress:Int? = null) {
    companion object Factory {
        fun create(): TaskList = TaskList()
    }
}