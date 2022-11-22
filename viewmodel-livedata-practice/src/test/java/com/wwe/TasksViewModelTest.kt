package com.wwe

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wwe.tasks.TasksViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TasksViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var tasksViewModel: TasksViewModel

    @Before
    fun setUp() {
        tasksViewModel = TasksViewModel()
    }

    @Test
    fun testAddNewTask() {
        // WHEN
        tasksViewModel.addNewTask()

        // THEN
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertNotNull(value.getContentIfNotHandled())
    }

    @Test
    fun testOpenTask() {
        // WHEN
        tasksViewModel.openTask("WWE")

        // THEN
        val value = tasksViewModel.openTaskEvent.getOrAwaitValue()
        assertEquals("WWE", value.getContentIfNotHandled())
    }
}