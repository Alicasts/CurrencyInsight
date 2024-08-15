package com.alicasts.currencyinsight.presentation.currency_pair_list

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alicasts.currencyinsight.R
import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CurrencyPairListActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(CurrencyPairListActivity::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testRecyclerViewIsDisplayed() {
        onView(withId(R.id.currencyPairsListRecyclerView))
            .check(matches(isDisplayed()))
    }


    @Test
    fun testErrorIsDisplayed() {
        val errorMessage = "Network Error"
        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.handleState(CurrencyPairListState(error = errorMessage))
            }
        }

        onView(withId(R.id.errorTextView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            .check(matches(withText(errorMessage)))

        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.currencyPairsListRecyclerView))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun testRecyclerViewIsUpdated() {
        val currencyPairs = listOf(
            CurrencyPairListItemModel("USD/EUR", "US Dollar/Euro"),
            CurrencyPairListItemModel("GBP/USD", "British Pound/US Dollar")
        )

        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.handleState(CurrencyPairListState(currencyPairList = currencyPairs))
            }
        }


        onView(withId(R.id.currencyPairsListRecyclerView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.errorTextView))
            .check(matches(not(isDisplayed())))
    }

}