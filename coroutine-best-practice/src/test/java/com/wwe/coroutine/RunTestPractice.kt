package com.wwe.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

// Ref: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/
class RunTestPractice {

    /** run on the thread that started the test, and will never run in parallel. */
    @Test
    fun testWithMultipleDelays() = runTest {
        launch {
            delay(1_000)
            println("1. $currentTime") // 1000
            delay(200)
            println("2. $currentTime") // 1200
            delay(2_000)
            println("4. $currentTime") // 3200
        }
        val deferred = async {
            delay(3_000)
            println("3. $currentTime") // 3000
            delay(500)
            println("5. $currentTime") // 3500
        }
        deferred.await()
    }

    @Test
    fun testFoo() = runTest {
        launch {
            println(1)   // executes during runCurrent()
            delay(1_000) // suspends until time is advanced by at least 1_000
            println(2)   // executes during advanceTimeBy(2_000)
            delay(500)   // suspends until the time is advanced by another 500 ms
            println(3)   // also executes during advanceTimeBy(2_000)
            delay(5_000) // will suspend by another 4_500 ms
            println(4)   // executes during advanceUntilIdle()
        }
        // the child coroutine has not run yet
        runCurrent()

        // the child coroutine has called println(1), and is suspended on delay(1_000)
        advanceTimeBy(2_000) // progress time, this will cause two calls to `delay` to resume
        // the child coroutine has called println(2) and println(3) and suspends for another 4_500 virtual milliseconds

        advanceUntilIdle() // will run the child coroutine to completion

        assertEquals(6500, currentTime) // the child coroutine finished at virtual time of 6_500 milliseconds
    }

    @Test
    fun testWithMultipleDispatchers() = runTest {
        val scheduler = testScheduler // the scheduler used for this test
        val dispatcher1 = StandardTestDispatcher(scheduler, name = "IO dispatcher")
        val dispatcher2 = StandardTestDispatcher(scheduler, name = "Background dispatcher")
        launch(dispatcher1) {
            delay(1_000)
            println("1. $currentTime") // 1000
            delay(200)
            println("2. $currentTime") // 1200
            delay(2_000)
            println("4. $currentTime") // 3200
        }
        val deferred = async(dispatcher2) {
            delay(3_000)
            println("3. $currentTime") // 3000
            delay(500)
            println("5. $currentTime") // 3500
        }
        deferred.await()
    }

//    val scope = TestScope()
//
//    @BeforeTest
//    fun setUp() {
//        Dispatchers.setMain(StandardTestDispatcher(scope.testScheduler))
//        TestSubject.setScope(scope)
//    }
//
//    @AfterTest
//    fun tearDown() {
//        Dispatchers.resetMain()
//        TestSubject.resetScope()
//    }

    // @Test
    //fun testSubject() = scope.runTest {
    //    // the receiver here is `testScope`
    //}

    @Test
    fun testEagerlyEnteringChildCoroutines() = runTest(UnconfinedTestDispatcher()) {
        var entered = false
        val deferred = CompletableDeferred<Unit>()
        var completed = false
        launch {
            entered = true
            deferred.await()
            completed = true
        }
        assertTrue(entered) // `entered = true` already executed.
        assertFalse(completed) // however, the child coroutine then suspended, so it is enqueued.
        deferred.complete(Unit) // resume the coroutine.
        assertTrue(completed) // now the child coroutine is immediately completed.
    }

    @Test
    fun testEagerlyEnteringSomeChildCoroutines() = runTest(UnconfinedTestDispatcher()) {
        var entered1 = false
        launch {
            entered1 = true
        }
        assertTrue(entered1) // `entered1 = true` already executed

        var entered2 = false
        launch(StandardTestDispatcher(testScheduler)) {
            // this block and every coroutine launched inside it will explicitly go through the needed dispatches
            entered2 = true
        }
        assertFalse(entered2)
        runCurrent() // need to explicitly run the dispatched continuation
        assertTrue(entered2)
    }

//    @Test
//    fun testFooWithTimeout() = runTest {
//        assertFailsWith<TimeoutCancellationException> {
//            withTimeout(1_000) {
//                delay(999)
//                delay(2)
//                println("this won't be reached")
//            }
//        }
//    }

    suspend fun veryExpensiveFunction() = withContext(Dispatchers.Default) {
        delay(1_000)
        1
    }

    @Test
    fun testExpensiveFunction() = runTest {
        val result = veryExpensiveFunction() // will take a whole real-time second to execute
        // the virtual time at this point is still 0

        assert(1 == result)
    }
}