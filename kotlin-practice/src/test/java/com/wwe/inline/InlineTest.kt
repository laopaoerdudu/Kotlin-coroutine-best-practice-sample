package com.wwe.inline

import org.junit.Test

class InlineTest {

    @Test
    fun test() {
        execute {
            println("David")
            /**
             * this return statement happens directly in the main function and not in the lambda.
             * That’s the reason we can use normal returns inside inline functions.
             */
            return
        }
        println("Exit")
    }

    private inline fun execute(action: () -> Unit) {
        action()
    }

    @Test
    fun test2() {
        foo {
            println("Hello World")
            // return // 'return' is not allowed here
        }
    }

    private fun foo(f: () -> Unit) {
        f()
    }
}