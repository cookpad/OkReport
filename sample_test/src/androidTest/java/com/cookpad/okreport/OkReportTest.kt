package com.cookpad.okreport

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.cookpad.core.data.OkReportRepository
import io.victoralbertos.device_animation_test_rule.DeviceAnimationTestRule
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.hamcrest.core.Is.`is`
import org.junit.ClassRule
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class OkReportTest {
    private val titleReport = "Automated test"
    private val desStep1 = "desStep1"
    private val desStep2 = "desStep2"
    private val desStep3 = "desStep3"

    companion object {
        @ClassRule @JvmField var animRule = DeviceAnimationTestRule()
    }

    @Rule @JvmField var rule = ActivityTestRule(MainActivity::class.java)

    @Test fun step1VerifyHomeButtonAsBack() {
        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())
        onView(withContentDescription("Navigate up")).perform(click())
        onView(withId(R.id.ivBackground)).check(matches(isDisplayed()))
    }

    @Test fun step2VerifyTitleReport() {
        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())

        onView(withId(R.id.etTitle)).perform(click(), replaceText(titleReport), closeSoftKeyboard())
        onView(withContentDescription("Navigate up")).perform(click())

        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())
        onView(withId(R.id.etTitle)).check(matches(withText(titleReport)))
    }

    @Test fun step3VerifyRemoveStep() {
        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())

        removeSteps(1)

        onView(withId(R.id.rvSteps)).check(recyclerViewHasCount(3))
    }

    @Test fun step4VerifyRemoveAllStep() {
        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())
        removeSteps(4)
        onView(withId(R.id.ivBackground)).check(matches(isDisplayed()))
    }

    @Test fun step5VerifyTextStep() {
        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())
        onView(withContentDescription("Navigate up")).perform(click())

        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())
        onView(withContentDescription("Navigate up")).perform(click())

        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())

        onView(withId(R.id.rvSteps)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, replaceTextView(R.id.etStep, desStep1)))
        onView(withId(R.id.rvSteps)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, replaceTextView(R.id.etStep, desStep2)))
        onView(withId(R.id.rvSteps)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(2, replaceTextView(R.id.etStep, desStep3)))

        onView(withContentDescription("Navigate up")).perform(click())

        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())

        onView(withId(R.id.rvSteps)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, checkTextView(R.id.etStep, desStep1)))
        onView(withId(R.id.rvSteps)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, checkTextView(R.id.etStep, desStep2)))
        onView(withId(R.id.rvSteps)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(2, checkTextView(R.id.etStep, desStep3)))
    }

    @Test fun step6VerifySendReport() {
        clearSharedPreferences()

        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())

        removeSteps(4)

        onView(allOf(withId(R.id.menu_item_send_report), isDisplayed())).perform(click())

        onView(withId(android.R.id.input)).perform(click(), replaceText("authorTest"), closeSoftKeyboard())
        onView(allOf(withId(R.id.md_buttonDefaultPositive), isDisplayed())).perform(click())

        onView(withId(R.id.ivBackground)).check(matches(isDisplayed()))
    }

    @Test fun step7VerifySubtitleActionBar() {
        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())
        onView(withText("1 step")).check(matches(isDisplayed()))

        onView(withContentDescription("Navigate up")).perform(click())

        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())
        onView(withText("2 steps")).check(matches(isDisplayed()))
    }

    @Test fun step8VerifyCanvasActivity() {
        onView(allOf(withId(R.id.btTrigger), isDisplayed())).perform(click())

        onView(withId(R.id.rvSteps)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickChildViewWithId(R.id.btHighlight)))

        onView(withId(R.id.rlRoot)).check(matches(isDisplayed()))

        onView(withContentDescription("Navigate up")).perform(click())

        onView(withId(R.id.rvSteps)).check(matches(isDisplayed()))
    }

    private fun removeSteps(numberOf: Int) {
        for (i in 1..numberOf) {
            onView(withId(R.id.rvSteps)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickChildViewWithId(R.id.btRemoveStep)))
        }
    }

    private fun clearSharedPreferences() {
        OkReportRepository.saveAuthor(rule.activity, "")
    }
}

private fun clickChildViewWithId(id: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun getDescription(): String {
            return "Click on a child view with specified id."
        }

        override fun perform(uiController: UiController, view: View) {
            val v = view.findViewById<View>(id)
            v.performClick()
        }
    }
}

private fun checkTextView(id: Int, expected: String): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun getDescription(): String {
            return "Check text on a child view with specified id."
        }

        override fun perform(uiController: UiController, view: View) {
            val v = view.findViewById<TextView>(id)
            assertThat(v.text.toString(), `is`(expected))
        }
    }
}

private fun recyclerViewHasCount(expectedCount: Int): ViewAssertion {
    return ViewAssertion { view, noViewFoundException ->
        if (noViewFoundException != null) {
            throw noViewFoundException
        }

        val recyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        assertThat(adapter.itemCount, `is`(expectedCount))
    }
}

private fun replaceTextView(id: Int, text: String): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun getDescription(): String {
            return "Replace text on a child view with specified id."
        }

        override fun perform(uiController: UiController, view: View) {
            view.findViewById<TextView>(id).text = text
        }
    }
}
