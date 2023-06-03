package com.fimo.aidentist.ui.menu.auth

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.fimo.aidentist.R
import org.junit.Before
import org.junit.Test

class LoginActivityTest {
    private val dummyEmail = "testmvvm1@gmail.com"
    private val dummyPassword = "12345678"

    @Before
    fun setup() {
        ActivityScenario.launch(LoginActivity::class.java)
    }

    @Test
    fun testMoveToSignUpActivity() {
        onView(withId(R.id.tv_signup)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_signup)).perform(click())

        onView(withId(R.id.tv_login)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_login)).perform(click())
    }

    @Test
    fun testLoginSession() {
        onView(withId(R.id.emailEditText)).perform(typeText(dummyEmail), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(dummyPassword), closeSoftKeyboard())

        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonLogin)).perform(click())
    }
}