package com.wwe.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wwe.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

// Just for unit test sample
@ExperimentalCoroutinesApi
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

    private val _dataLoading = MutableLiveData<Boolean>(false)
    val dataLoading: LiveData<Boolean> = _dataLoading

    fun refresh() {
        _dataLoading.value = true
        viewModelScope.launch {
            refreshTasks()
            _dataLoading.value = false
        }
    }

    suspend fun refreshTasks() {
        println("refresh")
    }
}