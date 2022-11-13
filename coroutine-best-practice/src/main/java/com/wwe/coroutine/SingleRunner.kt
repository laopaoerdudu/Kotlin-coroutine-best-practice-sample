package com.wwe.coroutine

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**  ensure that only one sort is running at a time. */
class SingleRunner {

    /** A Mutex lets you ensure only one coroutine runs at a time — and they will finish in the order they started. */
    private val mutex = Mutex()

    suspend fun <T> afterPrevious(block: suspend () -> T): T {
        // Before running the block, ensure that no other blocks are running by taking a lock on the
        // mutex.

        // The mutex will be released automatically when we return.

        // If any other block were already running when we get here, it will wait for it to complete
        // before entering the `withLock` block.
        mutex.withLock {
            return block()
        }
    }
}