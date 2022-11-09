package com.wwe.coroutine

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.text.Layout
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import org.junit.Test
import kotlin.coroutines.resume

class DAY9Test {

    @Test
    fun test1() {
        // We can then use it like so
//        viewLifecycleOwner.lifecycleScope.launch {
//            // Make the view invisible and set some new text
//            titleView.isInvisible = true
//            titleView.text = "Hi everyone!"
//
//            // Wait for the next layout pass to know
//            // the height of the view
//            titleView.awaitNextLayout()
//
//            // Layout has happened!
//            // We can now make the view visible, translate it up, and then animate it
//            // back down
//            titleView.isVisible = true
//            titleView.translationY = -titleView.height.toFloat()
//            titleView.animate().translationY(0f)
//        }
    }

    @Test
    fun test2() {
        /**
         * In isolation these functions don’t do a lot, but when you start combining them together they become really powerful.
         */
        // viewLifecycleOwner.lifecycleScope.launch {
        //    ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f, 1f).run {
        //        start()
        //        awaitEnd()
        //    }
        //
        //    ObjectAnimator.ofFloat(imageView, View.TRANSLATION_Y, 0f, 100f).run {
        //        start()
        //        awaitEnd()
        //    }
        //
        //    ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -100f, 0f).run {
        //        start()
        //        awaitEnd()
        //    }
        //}
    }
}

suspend fun View.awaitNextLayout() = suspendCancellableCoroutine<Unit> { cont ->
    // This lambda is invoked immediately, allowing us to create
    // a callback/listener

    val listener = object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View?,
            p1: Int,
            p2: Int,
            p3: Int,
            p4: Int,
            p5: Int,
            p6: Int,
            p7: Int,
            p8: Int
        ) {
            // The next layout has happened!
            // First remove the listener to not leak the coroutine
            view?.removeOnLayoutChangeListener(this)
            // Finally resume the continuation, and
            // wake the coroutine up
            cont.resume(Unit)
        }
    }
    // If the coroutine is cancelled, remove the listener
    cont.invokeOnCancellation { removeOnLayoutChangeListener(listener) }
    // And finally add the listener to view
    addOnLayoutChangeListener(listener)

    // The coroutine will now be suspended. It will only be resumed
    // when calling cont.resume() in the listener above
}

suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> { cont ->
    // Add an invokeOnCancellation listener. If the coroutine is
    // cancelled, cancel the animation too that will notify
    // listener's onAnimationCancel() function
    cont.invokeOnCancellation { cancel() }

    addListener(object : AnimatorListenerAdapter() {
        private var endedSuccessfully = true

        override fun onAnimationCancel(animation: Animator) {
            // Animator has been cancelled, so flip the success flag
            endedSuccessfully = false
        }

        override fun onAnimationEnd(animation: Animator) {
            // Make sure we remove the listener so we don't keep
            // leak the coroutine continuation
            animation.removeListener(this)

            if (cont.isActive) {
                // If the coroutine is still active...
                if (endedSuccessfully) {
                    // ...and the Animator ended successfully, resume the coroutine
                    cont.resume(Unit)
                } else {
                    // ...and the Animator was cancelled, cancel the coroutine too
                    cont.cancel()
                }
            }
        }
    })
}

/**
 * Wait for the transition to complete so that the given [transitionId] is fully displayed.
 *
 * @param transitionId The transition set to await the completion of
 * @param timeout Timeout for the transition to take place. Defaults to 5 seconds.
 */
suspend fun Layout.awaitTransitionComplete(transitionId: Int, timeout: Long = 5000L) {
    // If we're already at the specified state, return now
    // if (currentState == transitionId) return

    var listener: MotionLayout.TransitionListener? = null

    try {
        withTimeout(timeout) {
            suspendCancellableCoroutine<Unit> { continuation ->
                val l = object : TransitionAdapter() {
                    override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                        if (currentId == transitionId) {
                            // removeTransitionListener(this)
                            continuation.resume(Unit)
                        }
                    }
                }
                // If the coroutine is cancelled, remove the listener
                continuation.invokeOnCancellation {
                    // removeTransitionListener(l)
                }
                // And finally add the listener
                // addTransitionListener(l)
                listener = l
            }
        }
    } catch (tex: TimeoutCancellationException) {
        // Transition didn't happen in time. Remove our listener and throw a cancellation
        // exception to let the coroutine know
        // listener?.let(::removeTransitionListener)
        throw CancellationException(
            "Transition to state with id: $transitionId did not" +
                    " complete in timeout.", tex
        )
    }
}

/**
 * Await an item in the data set with the given [itemId], and return its adapter position.
 */
suspend fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.awaitItemIdExists(itemId: Long): Int {
    val currentPos = 0 // findItemIdPosition(itemId)
    // If the item is already in the data set, return the position now
    if (currentPos >= 0) return currentPos

    // Otherwise we register a data set observer and wait for the item ID to be added
    return suspendCancellableCoroutine { continuation ->
        val observer = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (positionStart until positionStart + itemCount).forEach { position ->
                    // Iterate through the new items and check if any have our itemId
                    if (getItemId(position) == itemId) {
                        // Remove this observer so we don't leak the coroutine
                        unregisterAdapterDataObserver(this)
                        // And resume the coroutine
                        continuation.resume(position)
                    }
                }
            }
        }
        // If the coroutine is cancelled, remove the observer
        continuation.invokeOnCancellation {
            unregisterAdapterDataObserver(observer)
        }
        // And finally register the observer
        registerAdapterDataObserver(observer)
    }
}

suspend fun RecyclerView.awaitScrollEnd() {
    // If a smooth scroll has just been started, it won't actually start until the next
    // animation frame, so we'll await that first
    // awaitAnimationFrame()
    // Now we can check if we're actually idle. If so, return now
    if (scrollState == RecyclerView.SCROLL_STATE_IDLE) return

    suspendCancellableCoroutine<Unit> { continuation ->
        continuation.invokeOnCancellation {
            // If the coroutine is cancelled, remove the scroll listener
            // recyclerView.removeOnScrollListener(this)
            // We could also stop the scroll here if desired
        }

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Make sure we remove the listener so we don't leak the
                    // coroutine continuation
                    recyclerView.removeOnScrollListener(this)
                    // Finally, resume the coroutine
                    continuation.resume(Unit)
                }
            }
        })
    }
}

suspend fun View.awaitAnimationFrame() = suspendCancellableCoroutine<Unit> { continuation ->
    val runnable = Runnable {
        continuation.resume(Unit)
    }
    // If the coroutine is cancelled, remove the callback
    continuation.invokeOnCancellation { removeCallbacks(runnable) }
    // And finally post the runnable
    postOnAnimation(runnable)
}