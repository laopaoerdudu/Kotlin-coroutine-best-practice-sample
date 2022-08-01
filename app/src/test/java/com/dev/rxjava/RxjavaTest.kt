package com.dev.rxjava

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Ref: https://github.com/ReactiveX/RxJava/wiki/Error-Handling-Operators
 */
class RxjavaTest {

    private val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { testScheduler }
        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        testScheduler.advanceTimeTo(0, TimeUnit.SECONDS)
    }

    @After
    fun tearDown() {

    }

    @Test
    fun `test doOnError operator`() {
        // WHEN
        val testObserver = Observable.error<String>(IOException("wrong"))
            .doOnError { error ->
                println("The error message is ${error.message}")
            }.test()

        // THEN
        testObserver.apply {
            assertErrorMessage("wrong")
            assertNotComplete()
        }
        testObserver.dispose()
    }

    @Test
    fun `test onErrorResumeNext operator`() {
        // WHEN
        val testObserver = Observable.just("hello")
            .flatMap {
                Observable.error<String>(IOException("wrong"))
            }
            .onErrorResumeNext(Observable.just("WWE"))
            .test()

        // THEN
        testObserver.apply {
            assertNoErrors()
            assertComplete()
            assertResult("WWE")
        }
        testObserver.dispose()
    }

    /**
     * When hit `onError()` then trigger `retryWhen`
     */
    @Test
    fun `test retryWhen operator`() {
        // WHEN
        val testObserver = Observable.interval(0, 1, TimeUnit.SECONDS)
            .flatMap { value ->
                when {
                    value >= 2 -> Observable.error(IOException("wrong"))
                    else -> Observable.just(value)
                }
            }.retryWhen { error ->
                error.flatMap { throwable ->
                    if (throwable is IOException) {
                        Observable.just(0L)
                    } else {
                        Observable.error(throwable)
                    }
                }
            }.test()
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)

        // THEN
        testObserver.apply {
            assertNoErrors()
            assertNotComplete()
            assertValues(0, 1, 0, 0)
        }
        testScheduler.advanceTimeTo(0, TimeUnit.SECONDS)
        testObserver.dispose()
    }

    /**
     * When hit `onComplete()` then trigger `repeatWhen`
     */
    @Test
    fun `test repeatWhen operator`() {
        // WHEN
        val testObserver = Observable.just(1, 2)
            .repeatWhen { observable ->
                observable.delay(3, TimeUnit.SECONDS);
            }.test()
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)

        // THEN
        testObserver.apply {
            assertNoErrors()
            assertNotComplete()
            assertValues(1, 2, 1, 2)
        }

        testScheduler.advanceTimeTo(0, TimeUnit.SECONDS)
        testObserver.dispose()
    }

    /**
     * The `transform` operator best practice apply on `Glide` and `Picasso` framework
     */
    @Test
    fun `test transformer operator`() {
        // WHEN
        val convert = ObservableTransformer<Int, String> { upstream ->
            upstream.map {
                it.toString()
            }
        }
        val testObserver = Observable.just(123,456)
            .compose(convert).test()

        // THEN
        testObserver.apply {
            assertNoErrors()
            assertComplete()
            assertValues("123", "456")
        }
        testObserver.dispose()
    }
}