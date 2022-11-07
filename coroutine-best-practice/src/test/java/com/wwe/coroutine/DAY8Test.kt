package com.wwe.coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class DAY8Test {

    @get:Rule
    var coroutinesTestRule = MainDispatcherRule()

    private val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }

    @Test
    fun test() = runTest(coroutinesTestRule.testDispatcher) {
        // ...
        //  Calling `runTest` will make that coroutine to execute synchronously in the test.
    }

    @Test
    fun test_viewModelScope_case() = runTest(coroutinesTestRule.testDispatcher) {
        MainViewModel().sampleMethod()
    }

    @Test
    fun test_sampleMethod_withOtherCoroutineScope() = runTest(coroutinesTestRule.testDispatcher) {
        MainViewModel().sampleMethod_withOtherCoroutineScope()
    }
}

class MainViewModel: ViewModel() {

    private val handler = CoroutineExceptionHandler { context, exception ->
        println("Caught $exception")
    }

    fun sampleMethod() {
        viewModelScope.launch(handler) {
            launch {
               // throw CancellationException()
                //throw IOException("Bad net")
                delay(500)
                println("child1 done")
            }.join()

            launch {
                println("child2 done")
            }

            println("DONE")
        }
    }

    fun sampleMethod_withOtherCoroutineScope() {
        val scope = CoroutineScope(Job() + Dispatchers.Main + handler)
        scope.launch {
            launch {
                throw IOException("Bad net")
            }.join()

            launch {
                println("child2 done")
            }
        }
    }
}
