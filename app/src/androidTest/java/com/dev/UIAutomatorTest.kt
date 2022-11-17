package com.dev

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.runner.RunWith

private const val BASIC_PACKAGE = "com.dev.MainActivity"
private const val LAUNCH_TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class UIAutomatorTest {

    private lateinit var device: UiDevice

    @Before
    fun startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Start from the home screen
        device.pressHome()

        // Wait for launcher
        val launcherPackage: String = device.launcherPackageName
        assertThat(launcherPackage, notNullValue())
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT
        )

        // Launch the app
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(
            BASIC_PACKAGE)?.apply {
            // Clear out any previous instances
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(
            Until.hasObject(By.pkg(BASIC_PACKAGE).depth(0)),
            LAUNCH_TIMEOUT
        )
    }
}

// As a best practice, when specifying a selector, you should use a Resource ID (if one is assigned to a UI element) instead of a text element or content-descriptor.
//val cancelButton: UiObject = device.findObject(
//    UiSelector().text("Cancel").className("android.widget.Button")
//)
//val okButton: UiObject = device.findObject(
//    UiSelector().text("OK").className("android.widget.Button")
//)
//
//// Simulate a user-click on the OK button, if found.
//if (okButton.exists() && okButton.isEnabled) {
//    okButton.click()
//}
