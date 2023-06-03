package com.fimo.aidentist.ui.navigation.camera

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.fimo.aidentist.R
import org.junit.Before
import org.junit.Test

class CameraResultActivityTest {
    @Before
    fun setup() {
        ActivityScenario.launch(CameraResultActivity::class.java)
    }

    @Test
    fun testMoveToCameraActivity() {
        onView(withId(R.id.openCamera)).check(matches(isDisplayed()))
        onView(withId(R.id.openCamera)).perform(click())

        onView(withId(R.id.closeCamera)).check(matches(isDisplayed()))
        onView(withId(R.id.closeCamera)).perform(click())
    }

}