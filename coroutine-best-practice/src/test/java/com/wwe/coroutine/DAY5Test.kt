package com.wwe.coroutine

import kotlinx.coroutines.*
import org.junit.Test

class DAY5Test {

    @Test
    fun test1() {
        /** When creating a CoroutineScope it takes a CoroutineContext as a parameter to its constructor.  */
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        val job = scope.launch {
            // new coroutine
        }
    }

    @Test
    fun test2() {
        /**  an implicit task hierarchy is created. */
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        val job = scope.launch {

            // New coroutine that has CoroutineScope as a parent
            val result = withContext(Dispatchers.Default) {

                // New coroutine that has the coroutine started by
                // launch as a parent
            }
        }
    }

    @Test
    fun test3() = runBlocking {
        /** The coroutine work doesn't just stop when cancel is called. */
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) {
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("Hello ${i++}")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1000L)
        println("Cancel!")
        job.cancel()
        println("Done!")
    }

    @Test
    fun test3_isActive() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0

            /** Checking for job’s active state */
            while (i < 5 && isActive) {
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("Hello ${i++}")
                    nextPrintTime += 500L
                }
            }

            /** So now, when the coroutine is no longer active, the while will break and we can do our cleanup. */
            println("Clean up!")
        }
        delay(1000L)
        println("Cancel!")
        job.cancel()
        println("Done!")
    }

    @Test
    fun test3_ensureActive() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0

            /** Checking for job’s active state */
            while (i < 5) {
                ensureActive()

                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("Hello ${i++}")
                    nextPrintTime += 500L
                }
            }

            // the coroutine work is completed so we can cleanup
            println("Clean up!")
        }
        delay(1000L)
        println("Cancel!")
        job.cancel()
        println("Done!")
    }

    @Test
    fun test3_yield() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0

            /** Checking for job’s active state */
            while (i < 5) {
                yield()

                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("Hello ${i++}")
                    nextPrintTime += 500L
                }
            }

            // the coroutine work is completed so we can cleanup
            println("Clean up!")
        }
        delay(1000L)
        println("Cancel!")
        job.cancel()
        println("Done!")
    }

    @Test
    fun test4() = runBlocking {
        val job = launch(Dispatchers.Default) {
            try {
                work()
            } finally {
                withContext(NonCancellable) {
                    delay(1000L)
                    cleanUp()
                }
            }
        }
        delay(1000L)
        println("Cancel!")
        job.cancel()
        println("Done!")
    }

    private suspend fun work() {
        var nextPrintTime = System.currentTimeMillis()
        var i = 0
        while (i < 5) {
            yield()
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("Hello ${i++}")
                nextPrintTime += 500L
            }
        }
    }

    private suspend fun cleanUp() {
        println("Cleanup code")
    }
}