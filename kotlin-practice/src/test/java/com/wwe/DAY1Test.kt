package com.wwe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.CountDownLatch

/** Requirement: "现有 Task1、Task2 等多个并行任务，如何等待全部任务执行完成后，开始执行 Task3 ?" */
class DAY1Test {

    @Test
    fun test() {
        lateinit var s1: String
        lateinit var s2: String

        val cd = CountDownLatch(2)

        Thread {
            s1 = task1()
            cd.countDown()
        }.start()

        Thread {
            s2 = task2()
            cd.countDown()
        }.start()

        cd.await()

        task3(s1, s2)
    }

    private fun task1(): String {
        return "Hello task1"
    }

    private fun task2(): String {
        return "Hello task2"
    }

    private fun task3(var0: String, var1: String) {
        println("$var0-$var1")
    }

    @Test
    fun test2() {
        runBlocking {
            val c1 = async(Dispatchers.IO) {
                task1()
            }

            val c2 = async(Dispatchers.IO) {
                task2()
            }

            task3(c1.await(), c2.await())
        }
    }
}