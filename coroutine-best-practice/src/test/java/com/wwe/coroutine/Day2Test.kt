package com.wwe.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

/** 协程具有结构化的特点，`SupervisorJob` 仅只能用于同一级别的子协程。*/
class Day2Test {

    @Test
    fun test() = runTest {
        val scope = CoroutineScope(CoroutineExceptionHandler { _, _ -> })
        scope.launch(SupervisorJob()) {
            /**
             * 子协程在 launch 时会创建新的协程作用域，其会使用默认新的 Job 替代我们传递 SupervisorJob，所以导致我们传递的 SupervisorJob 被覆盖。
             * 所以如果我们想让子协程不影响父协程或者其他子协程，此时就必须再显示添加 SupervisorJob。 */
            launch(CoroutineName("A")) {
                //delay(10)
                throw RuntimeException()
            }
            launch(CoroutineName("B")) {
                //delay(100)
                println("B invoked")
            }
        }
    }

    @Test
    fun test1() = runTest {
        val scope = CoroutineScope(CoroutineExceptionHandler { _, _ -> })
        scope.launch(SupervisorJob()) {
            launch(CoroutineName("A") + SupervisorJob()) {
                //delay(10)
                throw RuntimeException()
            }
            launch(CoroutineName("B")) {
                //delay(200)
                println("B invoked")
            }
        }
    }

    @Test
    fun test2() = runTest {
        val scope = CoroutineScope(SupervisorJob())
        // val scope = CoroutineScope(Job())
        scope.launch {
            //throw IOException("Bad net")
            throw CancellationException("Bad net")
        }
        scope.launch {
            // child#2 will not be cancelled.
            println("#child2 done")
        }
    }
}