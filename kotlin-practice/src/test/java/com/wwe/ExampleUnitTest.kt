package com.wwe

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test1() {
        doWithTry {
            // 添加会出现异常的代码, 例如
            val result = 1 / 0
        }
    }

    private inline fun <T> doWithTry(block: () -> T) {
        try {
            block()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @Test
    fun test2() {
        val data = (1..3).asSequence()
            .filter { print("Filter $it, "); it % 2 == 1 }
            .map { print("Map $it, "); it * 2 }
            .forEach { print("Each $it, ") }
        println(data)
    }

    @Test
    fun test3() {
        val data = (1..3).asIterable()
            .filter { print("Filter $it, "); it % 2 == 1 }
            .map { print("Map $it, "); it * 2 }
            .forEach { print("Each $it, ") }
        println(data)
    }

    @Test
    fun testRemoveStringPrefix() {
        // GIVEN
        val data = " David"

        // WHEN
        val result = data.removePrefix(" ")

        // THEN
        assertEquals("David", result)
    }

    @Test
    fun testRemoveStringSuffix() {
        // GIVEN
        val data = "David "

        // WHEN
        val result = data.removeSuffix(" ")

        // THEN
        assertEquals("David", result)
    }

    @Test
    fun testRemoveStringPrefixAndSuffix() {
        // GIVEN
        val data = " David "

        // WHEN
        val result = data.removeSurrounding(" ")

        // THEN
        assertEquals("David", result)
    }

    @Test
    fun testRemoveStringPrefixAndSuffixForList() {
        // GIVEN
        val data = "{JAVA | KOTLIN | C++ | PYTHON}"

        // WHEN
        val result = data.removeSurrounding("{", "}")

        // THEN
        assertEquals("JAVA | KOTLIN | C++ | PYTHON", result)
    }

    @Test
    fun testString1() {
        // GIVEN
        val data = "*Hi David*"

        // WHEN
        val result = data.substringAfter("*")

        // THEN
        assertEquals("Hi David*", result)
    }

    @Test
    fun testString2() {
        // GIVEN
        val data = "*Hi David>"

        // WHEN
        val result = data.substringBefore(">")

        // THEN
        assertEquals("*Hi David", result)
    }

    @Test
    fun testString3() {
        // GIVEN
        val data = "*Hi David*"

        // WHEN
        val result = data.substringBefore("--")

        // THEN
        assertEquals("*Hi David*", result)
    }

    @Test
    fun testString4() {
        // GIVEN
        val data = "*Hi David*"

        // WHEN
        val result = data.substringAfter("--", "Game over")

        // THEN
        assertEquals("Game over", result)
    }

    @Test
    fun test4() {
        val s1 = Salary(10)
        val s2 = Salary(20)

        assertEquals("30", (s1 + s2).toString())
    }

    operator fun Salary.plus(other: Salary): Salary = Salary(base + other.base)
    operator fun Salary.minus(other: Salary): Salary = Salary(base - other.base)

    data class Salary(var base: Int = 100){
        override fun toString(): String = base.toString()
    }

    /** List convert to Map */
    @Test
    fun test5() {
        val users = listOf(User(1, "Michal"), User(2, "Marek"))
        val map = users.associateBy { it.id }

        assert(map == mapOf(1 to User(1, "Michal"), 2 to User(2, "Marek")) )
    }

    fun <T : Comparable<T>> List<T>.quickSort(): List<T> =
        if(size < 2) this
        else {
            val pivot = first()
            val (smaller, greater) = drop(1).partition { it <= pivot}
            smaller.quickSort() + pivot + greater.quickSort()
        }
}