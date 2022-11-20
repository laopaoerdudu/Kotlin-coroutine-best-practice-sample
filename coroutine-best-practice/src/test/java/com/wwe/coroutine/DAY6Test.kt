package com.wwe.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

/**
 * Remember to use `SupervisorJob` when you want to avoid propagating cancellation when an exception happens, and Job otherwise.
 * Uncaught exceptions will be propagated, catch them to provide a great UX!
 */
class DAY6Test {

    @Test
    fun test_supervisorScopeBehavior_badPractice() = runTest {
        val scope = CoroutineScope(Job())
        // SupervisorJob does nothing in this code!
        val job = scope.launch(SupervisorJob()) {
            launch {
                throw IOException("Bad net")
            }
            launch {
                // child#2 will be cancelled.
                println("#child2 done")
            }
            println("#parrent done")
        }

        job.join()
    }

    @Test
    fun test_supervisorScopeBehavior_goodPractice() = runTest {
        val scope = CoroutineScope(Job())
        scope.launch {
            supervisorScope {
                launch {
                    throw IOException("Bad net")
                }
                launch {
                    println("#child2 done")
                }
                println("#supervisorScope done")
            }
            println("#parrent done")
        }
    }

    @Test
    fun test_supervisorScopeBehavior_goodPractice2() = runTest {
        val scope = CoroutineScope(SupervisorJob())
        scope.launch {
            throw IOException("Bad net")
        }
        scope.launch {
            // child#2 will not be cancelled.
            println("#child2 done")
        }
    }

    @Test
    fun test_lauch_handle_exception() = runTest {
        coroutineScope {
            try {
                launch {
                    /** With launch, exceptions will be thrown as soon as they happen */
                    throw IOException("Bad net")
                }
            } catch (e: Exception) {
                /**
                 * using `coroutineScope`, Exception thrown in async WILL NOT be caught here
                 * but propagated up to the scope
                 */
                println("WILL NOT catch Exception")
            }
        }
    }

    @Test
    fun test_asynOperation_handle_exception() = runTest {
        /**
         *  Exceptions thrown in a `coroutineScope` builder or in coroutines created by other coroutines won’t be caught in a try/catch!
         *  The reason is that will automatically propagate the exception up to its parent that will throw the exception.
         */
        coroutineScope {
            val deferred = async {
                throw IOException("Bad net")
            }
            try {
                deferred.await()
            } catch (e: Exception) {
                /**
                 * using `coroutineScope`, Exception thrown in async WILL NOT be caught here
                 * but propagated up to the scope
                 */
                println("WILL NOT catch Exception")
                e.printStackTrace()
            }
        }
    }

    @Test
    fun test_supervisorScope_handle_exception() = runTest {
        supervisorScope {
            val deferred = async {
                throw IOException("Bad net")
            }
            try {
                deferred.await()
            } catch (e: Exception) {
                println("WILL catch the Exception")
            }
        }
    }

    private val handler = CoroutineExceptionHandler { context, exception ->
        println("Caught $exception")
    }

    @Test
    fun test_CoroutineExceptionHandler_caughtException() = runTest {
        val scope = CoroutineScope(Job())
        // the exception will be caught by the handler:
        scope.launch(handler) {
            launch {
                throw Exception("Failed coroutine")
            }
        }
    }

    @Test
    fun test_CoroutineExceptionHandler_uncaughtException() = runTest {
        val scope = CoroutineScope(Job())
        scope.launch {
            // the handler is installed in a inner coroutine, it won’t be caught exception
            /**
             * The inner launch will propagate the exception up to the parent as soon as it happens,
             * since the parent does’t know anything about the handler,
             * the exception will be thrown.
             */
            launch(handler) {
                throw Exception("Failed coroutine")
            }
        }
    }
}