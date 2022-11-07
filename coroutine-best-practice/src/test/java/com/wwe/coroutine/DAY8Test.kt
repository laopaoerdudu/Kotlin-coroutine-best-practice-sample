package com.wwe.coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DAY8Test {

    @get:Rule
    var coroutinesTestRule = MainDispatcherRule()

    @Test
    fun test() = runTest(coroutinesTestRule.testDispatcher) {
        // ...
        //  Calling `runTest` will make that coroutine to execute synchronously in the test.
    }

    @Test
    fun test_viewModelScope_case() = runTest(coroutinesTestRule.testDispatcher) {
        MainViewModel().sampleMethod()
    }
}

class MainViewModel : ViewModel() {

    private val handler = CoroutineExceptionHandler { context, exception ->
        println("Caught $exception")
    }

    fun sampleMethod() {
        viewModelScope.launch(handler) {
            launch {
                delay(500)
                throw CancellationException()
                println("job done")
            }.join()

            launch {
                println("child done")
            }
        }
    }
}
