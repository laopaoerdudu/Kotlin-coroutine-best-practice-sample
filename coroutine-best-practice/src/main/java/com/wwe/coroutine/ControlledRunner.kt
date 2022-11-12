package com.wwe.coroutine

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference

class ControlledRunner<T> {
    private val activeTask = AtomicReference<Deferred<T>?>(null)

    suspend fun cancelPreviousThenRun(block: suspend() -> T): T {
        // fast path: if we already know about an active task, just cancel it right away.
        activeTask.get()?.cancelAndJoin()

        return coroutineScope {
            // Create a new coroutine, but don't start it until it's decided that this block should
            // execute. In the code below, calling await() on newTask will cause this coroutine to
            // start.
            val newTask = async(start = CoroutineStart.LAZY) {
                block()
            }

            // When newTask completes, ensure that it resets activeTask to null (if it was the
            // current activeTask).
            newTask.invokeOnCompletion {
                activeTask.compareAndSet(newTask, null)
            }

            // Kotlin ensures that we only set result once since it's a val, even though it's set
            // inside the while(true) loop.
            val result: T

            // Loop until we are sure that newTask is ready to execute (all previous tasks are
            // cancelled)
            while(true) {
                if (!activeTask.compareAndSet(null, newTask)) {
                    // some other task started before newTask got set to activeTask, so see if it's
                    // still running when we call get() here. If so, we can cancel it.

                    // we will always start the loop again to see if we can set activeTask before
                    // starting newTask.
                    activeTask.get()?.cancelAndJoin()
                    // yield here to avoid a possible tight loop on a single threaded dispatcher
                    yield()
                } else {
                    // happy path - we set activeTask so we are ready to run newTask
                    result = newTask.await()
                    break
                }
            }

            // Kotlin ensures that the above loop always sets result exactly once, so we can return
            // it here!
            result
        }
    }

}