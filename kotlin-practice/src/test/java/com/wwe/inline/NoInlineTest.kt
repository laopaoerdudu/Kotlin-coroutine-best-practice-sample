package com.wwe.inline

import org.junit.Test

/** noinline 是局部关闭内联优化 */
class NoInlineTest {

    @Test
    fun test() {
        executeAll(
            {
                println("BIAO")
            },
            {
                println("ZHANG")
            }
        )
    }

    /**
     * Kotlin will still inline the executeAll() method call and action1 lambda. However,
     * it won’t do the same for the action2 lambda function because of the noinline modifier.
     */
    private inline fun executeAll(action1: () -> Unit, noinline action2: () -> Unit): () -> Unit {
        action1()
        return action2
    }
}