package com.wwe.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wwe.Event

class TasksViewModel : ViewModel() {

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>> = _newTaskEvent

    fun addNewTask() {
        _newTaskEvent.value = Event(Unit)
    }

    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent: LiveData<Event<String>> = _openTaskEvent

    fun openTask(taskId: String) {
        _openTaskEvent.value = Event(taskId)
    }
}