package com.wwe.coroutine

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference

// Ref: https://medium.com/androiddevelopers/coroutines-on-android-part-iii-real-work-2ba8a2ec2f45
class ControlledRunner<T> {
    private val activeTask = AtomicReference<Deferred<T>?>(null)

    /**
     * By calling [cancelPreviousThenRun], the old task will *always* be cancelled and then the new task will
     * be run. This is useful in situations where a new event implies that the previous work is no
     * longer relevant such as sorting or filtering a list.
     */
    suspend fun cancelPreviousThenRun(block: suspend () -> T): T {
        // fast path: if we already know about an active task, just cancel it right away.
        activeTask.get()?.cancelAndJoin()

        return coroutineScope {
            val newTask = async(start = CoroutineStart.LAZY) {
                block()
            }

            // When newTask completes, ensure that it resets activeTask to null
            newTask.invokeOnCompletion {
                activeTask.compareAndSet(newTask, null)
            }

            // Kotlin ensures that we only set result once since it's a val
            val result: T

            while (true) {
                if (!activeTask.compareAndSet(null, newTask)) {
                    activeTask.get()?.cancelAndJoin()
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

    /**  If there’s already a request running, it waits for the result of current “in flight” request and returns that instead of running a new one.
     * The block will only be executed if there was not already a request running.
     */
    suspend fun joinPreviousOrRun(block: suspend () -> T): T {
        activeTask.get()?.let {
            return it.await()
        }
        return coroutineScope {
            val newTask = async(start = CoroutineStart.LAZY) {
                block()
            }

            newTask.invokeOnCompletion {
                activeTask.compareAndSet(newTask, null)
            }

            val result: T

            while (true) {
                if (!activeTask.compareAndSet(null, newTask)) {
                    // some other task started before newTask got set to activeTask, so see if it's
                    // still running when we call get() here. There is a chance that it's already
                    // been completed before the call to get, in which case we need to start the
                    // loop over and try again.
                    val currentTask = activeTask.get()
                    if (currentTask != null) {
                        // happy path - we found the other task so use that one instead of newTask
                        newTask.cancel()
                        result = currentTask.await()
                        break
                    } else {
                        // retry path - the other task completed before we could get it, loop to try
                        // setting activeTask again.

                        // call yield here in case we're executing on a single threaded dispatcher
                        // like Dispatchers.Main to allow other work to happen.
                        yield()
                    }
                } else {
                    result = newTask.await()
                    break
                }
            }
            result
        }
    }
}