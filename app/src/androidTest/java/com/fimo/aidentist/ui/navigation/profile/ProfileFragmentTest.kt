package com.fimo.aidentist.ui.navigation.profile

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Assert.*
import com.fimo.aidentist.R
import org.junit.Before
import org.junit.Test

class ProfileFragmentTest {
    @Before
    fun setup() {
        launchFragmentInContainer<ProfileFragment>()
    }

    @Test
    fun testLogoutButton() {
        onView(withId(R.id.buttonLogout)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonLogout)).perform(click())
    }

    @Test
    fun testHistoryButton() {
        onView(withId(R.id.buttonHistory)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonHistory)).perform(click())

        onView(withId(R.id.button_back)).check(matches(isDisplayed()))
        onView(withId(R.id.button_back)).perform(click())
    }
}