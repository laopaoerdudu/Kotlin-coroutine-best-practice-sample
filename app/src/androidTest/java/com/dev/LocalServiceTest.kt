package com.dev

import android.content.Intent
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.ServiceTestRule
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.any
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException

@MediumTest
@RunWith(AndroidJUnit4::class)
class LocalServiceTest {

    @get:Rule
    val mServiceRule = ServiceTestRule()

    @Test
    @Throws(TimeoutException::class)
    fun testWithBoundService() {
        // GIVEN
        val serviceIntent = Intent(getApplicationContext(), LocalService::class.java).apply {
            putExtra(LocalService.SEED_KEY, 42L)
        }
        val binder = mServiceRule.bindService(serviceIntent)

        // WHEN
        val service: LocalService? = (binder as? LocalService.LocalBinder)?.service

        // THEN
        assertThat(
            service?.getRandomInt(), CoreMatchers.`is`(
                any(
                    Int::class.java
                )
            )
        )
    }
}