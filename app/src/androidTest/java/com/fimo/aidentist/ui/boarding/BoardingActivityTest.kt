package com.fimo.aidentist.ui.boarding

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.fimo.aidentist.R
import org.junit.Before
import org.junit.Test

class BoardingActivityTest {
    @Before
    fun setup() {
        ActivityScenario.launch(BoardingActivity::class.java)
    }

    @Test
    fun testButtonClick() {

        onView(withId(R.id.motionLayout)).perform(swipeRight())

        onView(withId(R.id.button_get_started)).check(matches(isDisplayed()))
        onView(withId(R.id.button_get_started)).perform(click())
    }
}