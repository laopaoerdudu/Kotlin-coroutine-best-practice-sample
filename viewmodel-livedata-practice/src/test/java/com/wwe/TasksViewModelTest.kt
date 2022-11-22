package com.wwe

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wwe.tasks.TasksViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TasksViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

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

    @Test
    fun testRefresh() {
        // WHEN
        tasksViewModel.refresh()

        // THEN
        assertTrue(tasksViewModel.dataLoading.getOrAwaitValue())
    }
}