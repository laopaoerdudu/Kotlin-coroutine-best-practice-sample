package com.wwe

import org.junit.Test

class Day6Test {

    @Test
    fun test() {
        // GIVEN
        // val appTree: Forest<Tree?> = Forest(null)
    }
}

data class Tree(val name: String)
data class Forest<T : Tree>(val t: T)
