package com.example.todoapp.tasks.data

data class TaskItems(val title:String, val taskName:String, var progress:Int, var isChecked: Boolean = false)

/*
data class TaskItems(var objectId: String? = null, val title:String? = null, val taskName:String? = null, var progress:Int? = null, var isChecked: Boolean = false) {
    companion object Factory {
        fun create(): TaskItems = TaskItems()
    }
}
 */