package com.wwe.inline

import org.junit.Test

/** 局部加强内联优化 */
class CrossInlineTest {

    @Test
    fun test() {
        println("doSth start")
        doSthElse {
            println("doSth else")
            return // This is `non-local` returns
        }
        println("doSth end")
    }

    private inline fun doSthElse(abc: () -> Unit) {
        abc()
    }

    /** This is how the `crossinline` can help us to avoid the "non-local returns". */
    @Test
    fun test_CrossInline() {
        print("doSomething start")
        doSthElse_CrossInline {
            print("doSomethingElse")
            // return // return is not allowed here
        }
        print("doSomething end")
    }

    /** We need to add the `crossinline`, then it will not allow us the put the return inside that lambdas like below: */
    private inline fun doSthElse_CrossInline(crossinline abc: () -> Unit) {
        abc()
    }
}