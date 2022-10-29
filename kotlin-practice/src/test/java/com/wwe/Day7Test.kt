package com.wwe

import com.wwe.delegate.PersonPlus
import com.wwe.lazy.Student
import org.junit.Test

class Day7Test {

    @Test
    fun test() {
        // WHEN
        val person = PersonPlus().apply {
            name = "BIAO"
            lastname = "ZHANG"
        }

        // THEN
        assert("Biao-4" == person.name)
        assert("Zhang-5" == person.lastname)
        assert(2 == person.updateCount)
    }

    @Test
    fun testLazy() {
        val student = Student("David")
        student.emails
        student.emails

        // 可以看到，两次访问 emails 属性，只有第一次调用了 loadEmailsByName 方法。
        // 如果把访问 emails 属性注释掉，会看到没有任何打印，说明 emails 属性确实是惰性初始化的。
    }
}