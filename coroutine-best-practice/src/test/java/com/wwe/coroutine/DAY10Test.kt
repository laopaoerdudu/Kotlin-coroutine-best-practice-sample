package com.wwe.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

/** kotlinx.coroutines 中所有挂起函数（带有 suspend 关键字函数）都是可以被取消的。
 * suspend 函数会检查协程是否需要取消并在取消时抛出 CancellationException。
 */
class DAY10Test {

    @Test
    fun test() = runTest {
        coroutineScope {
            val job = launch(Dispatchers.Default) {
                repeat(5) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500)
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // 协程抛出 Exception 直接终止了。
            println("main: Now I can quit.")
        }
    }

    @Test
    fun test1() = runTest {
        coroutineScope {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0

                // 显式检查取消状态
                while (isActive) {
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // cancels the job and waits for its completion
            println("main: Now I can quit.")
        }
    }

    @Test
    fun test2() = runTest {
        coroutineScope {
            val job = launch {
                try {
                    repeat(1000) { i ->
                        println("job: I'm sleeping $i ...")
                        delay(500L)
                    }
                } finally {
                    withContext(NonCancellable) {
                        delay(1000L)
                        println("finally clean up!")
                    }
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // cancels the job and waits for its completion
            println("main: Now I can quit.")
        }
    }
}