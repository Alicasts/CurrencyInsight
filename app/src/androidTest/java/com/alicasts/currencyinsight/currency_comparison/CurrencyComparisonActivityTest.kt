package com.alicasts.currencyinsight.currency_comparison

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alicasts.currencyinsight.R
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyComparisonDetails
import com.alicasts.currencyinsight.presentation.currency_comparison.CurrencyComparisonActivity
import com.alicasts.currencyinsight.presentation.currency_comparison.CurrencyComparisonState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.allOf

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CurrencyComparisonActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(CurrencyComparisonActivity::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testProgressBarIsDisplayedWhenLoading() {
        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.handleState(CurrencyComparisonState(isLoading = true))
            }
        }

        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))
        onView(withId(R.id.cards_fragment_container))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.error_text))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun testErrorIsDisplayed() {
        val errorMessage = "Network Error"
        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.handleState(CurrencyComparisonState(error = errorMessage))
            }
        }

        onView(withId(R.id.error_text))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            .check(matches(withText(errorMessage)))

        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.cards_fragment_container))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun testComparisonDetailsAreDisplayed() {
        val comparisonDetails = CurrencyComparisonDetails(
            code = "USD",
            codein = "BRL",
            name = "US Dollar/Real",
            high = "5.40",
            low = "5.20",
            varBid = "0.05",
            pctChange = "0.9",
            bid = "5.25",
            ask = "5.26",
            timestamp = "1650302400",
            createDate = "2022-04-19",
            historicalData = emptyList()
        )

        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.handleState(CurrencyComparisonState(comparisonDetails = comparisonDetails))
            }
        }

        onView(allOf(withId(R.id.currency_value_text_view), withParent(withId(R.id.target_currency_layout))))
            .check(matches(withText("1")))

        onView(allOf(withId(R.id.currency_value_text_view), withParent(withId(R.id.base_currency_layout))))
            .check(matches(withText("5.25")))

        onView(allOf(withId(R.id.currency_name_text_view), withParent(withId(R.id.target_currency_layout))))
            .check(matches(withText("US Dollar")))

        onView(allOf(withId(R.id.currency_name_text_view), withParent(withId(R.id.base_currency_layout))))
            .check(matches(withText("Real")))
    }

    @Test
    fun testChartIsDisplayedWhenComparisonDetailsArePresent() {
        val comparisonDetails = CurrencyComparisonDetails(
            code = "USD",
            codein = "BRL",
            name = "US Dollar/Real",
            high = "5.40",
            low = "5.20",
            varBid = "0.05",
            pctChange = "0.9",
            bid = "5.25",
            ask = "5.26",
            timestamp = "1650302400",
            createDate = "2022-04-19",
            historicalData = emptyList()
        )

        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.handleState(CurrencyComparisonState(comparisonDetails = comparisonDetails))
            }
        }

        onView(withId(R.id.compose_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCurrencyValueUpdatesCorrectly() {
        val comparisonDetails = CurrencyComparisonDetails(
            code = "USD",
            codein = "BRL",
            name = "US Dollar/Real",
            high = "5.40",
            low = "5.20",
            varBid = "0.05",
            pctChange = "0.9",
            bid = "5.25",
            ask = "5.26",
            timestamp = "1650302400",
            createDate = "2022-04-19",
            historicalData = emptyList()
        )

        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.handleState(CurrencyComparisonState(comparisonDetails = comparisonDetails))
            }
        }

        onView(allOf(withId(R.id.currency_value_text_view), withParent(withId(R.id.target_currency_layout))))
            .perform(replaceText("10"))

        onView(allOf(withId(R.id.currency_value_text_view), withParent(withId(R.id.base_currency_layout))))
            .check(matches(withText("52.5")))
    }

    @Test
    fun testSetViewsVisibilityForLoadingState() {
        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.handleState(CurrencyComparisonState(isLoading = true))
            }
        }

        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))

        onView(withId(R.id.cards_fragment_container))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.error_text))
            .check(matches(not(isDisplayed())))
    }
}