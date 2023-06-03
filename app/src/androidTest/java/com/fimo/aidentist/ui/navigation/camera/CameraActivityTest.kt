package com.fimo.aidentist.ui.navigation.camera

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.fimo.aidentist.R
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Test

class CameraActivityTest {
    @Before
    fun setup() {
        ActivityScenario.launch(CameraActivity::class.java)
    }

    @Test
    fun testChangeCamera() {
        onView(withId(R.id.switchCamera)).check(matches(isDisplayed()))
        onView(withId(R.id.switchCamera)).perform(click())
    }

    @Test
    fun testButtonCapture() {
        onView(withId(R.id.camera_capture_button)).check(matches(isDisplayed()))
        onView(withId(R.id.camera_capture_button)).perform(click())
    }

    @Test
    fun testRetakeButton() {
        onView(withId(R.id.camera_capture_button)).check(matches(isDisplayed()))
        onView(withId(R.id.camera_capture_button)).perform(click())

        onView(withId(R.id.retake)).check(matches(isDisplayed()))
        onView(withId(R.id.retake)).perform(click())
    }

    @Test
    fun testCheckButton() {
        onView(withId(R.id.camera_capture_button)).check(matches(isDisplayed()))
        onView(withId(R.id.camera_capture_button)).perform(click())

        onView(withId(R.id.check)).check(matches(allOf(isDisplayed(), isEnabled())))
        onView(withId(R.id.check)).perform(click())
    }
}