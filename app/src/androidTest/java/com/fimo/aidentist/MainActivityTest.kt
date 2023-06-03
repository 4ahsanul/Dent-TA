package com.fimo.aidentist

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Before
import org.junit.Test

class MainActivityTest {
    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun testMoveToHome() {
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.homeFragment)).perform(click())
    }

    @Test
    fun testMoveToProfile() {
        onView(withId(R.id.profileFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.profileFragment)).perform(click())
    }

    @Test
    fun testMoveToResultCamera() {
        onView(withId(R.id.cameraResultActivity)).check(matches(isDisplayed()))
        onView(withId(R.id.cameraResultActivity)).perform(click())

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.button_back)).perform(click())
    }
}