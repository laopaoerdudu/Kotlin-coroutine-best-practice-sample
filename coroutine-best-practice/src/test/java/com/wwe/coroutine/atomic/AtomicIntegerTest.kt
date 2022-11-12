package com.wwe.coroutine.atomic

import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicReference

class AtomicIntegerTest {

    @Test
    fun test() {
        // GIVEN
        val count = AtomicReference(0)

        // WHEN
        /** Compare the reference to the expected value, and if they are equal, set a new reference inside the AtomicReference object. */
        count.compareAndSet(0, 2)

        // THEN
        assert(2 == count.get())
    }

    @Test
    fun test2() {
        // GIVEN
        val executorService = Executors.newCachedThreadPool()
        val semaphore = Semaphore(200)
        for (i in 0 until 5000) {
            executorService.execute {
                try {
                    semaphore.acquire()
                    update(i)
                    semaphore.release()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

        }

        // WHEN
        executorService.shutdown()

        // THEN
        println("money = ${activeUser.get().money}")
        println("age = ${activeUser.get().age}")
    }

    private val activeUser = AtomicReference(User(0, 0))

    /** 如果线程安全的话，age 的值和 money 的值是一致的 */
    // @Synchronized
    private fun update(number: Int) {
        // 自定义的对象在访问时用的是 set, get 没有 CAS,所以导致线程不安全.
        activeUser.get().money = activeUser.get().money + number
        activeUser.get().age = activeUser.get().age + number
    }




}