package com.wwe.coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

    private val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }

    fun sampleMethod() {
        viewModelScope.launch(handler) {
                launch {
                    delay(500)
                    work3()
                }.join()
                launch {
                    work4()
                }.join()
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
            }.join()
        }
    }

    suspend fun work1() = suspendCancellableCoroutine<Unit> { continuation ->
        println("Work1 done")
        continuation.resumeWithException(IOException("Bad net"))
        //continuation.resume(Unit)

        continuation.invokeOnCancellation {
            /** We’re adding cancellation support to our suspending functions, everything will be automatically cleaned-up if this happened. */
            println("Work1 Clean up")
        }
    }

    suspend fun work2() = suspendCancellableCoroutine<Unit> { continuation ->
        println("Work2 done")
        continuation.resume(Unit)

        continuation.invokeOnCancellation {
            println("Work2 Clean up")
        }
    }

    suspend fun work3() = suspendCancellableCoroutine<Unit> { continuation ->
        if (continuation.isActive) {
            // println("Work3 done")
            // continuation.resume(Unit)
            // continuation.cancel(IOException("Bad net"))
            continuation.cancel(CancellationException("cancel"))
        }
        continuation.invokeOnCancellation { println("Work3 clean up") }
    }

    suspend fun work4() = suspendCancellableCoroutine<Unit> { continuation ->
        if (continuation.isActive) {
            println("Work4 done")
            continuation.resume(Unit)
        }
        continuation.invokeOnCancellation { println("Work4 clean up") }
    }
}
