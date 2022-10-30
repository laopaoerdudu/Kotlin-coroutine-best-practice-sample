package com.wwe.inline

import org.junit.Test

/** 局部加强内联优化 */
class CrossInlineTest {

    @Test
    fun test() {
        hello {
            return
        }
    }

    inline fun hello(postAction: () -> Unit) {
        postAction()
    }


}