package com.alicasts.currencyinsight.currency_comparison.details_fragment.chart

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyHistoricalData
import com.alicasts.currencyinsight.presentation.currency_comparison.details_fragment.chart.BidChartComposable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BidChartComposableTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private val comparisonDetails = CurrencyComparisonDetails(
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
        historicalData = listOf(
            CurrencyHistoricalData("5.40", "5.20", "0.05", "0.9", "5.25", "5.26", "1650302400")
        )
    )

    @Before
    fun init() {
        hiltRule.inject()

        composeTestRule.setContent {
            BidChartComposable(currencyComparisonDetails = comparisonDetails)
        }
    }

    @Test
    fun testBidChartComposableIsDisplayed() {
        composeTestRule.onNodeWithTag("BidChart").assertExists()
    }

    @Test
    fun testZoomButtonsWorkCorrectly() {
        composeTestRule.onNodeWithText("+").assertIsEnabled()
        composeTestRule.onNodeWithText("+").performClick()

        composeTestRule.onNodeWithText("-").assertIsEnabled()
        composeTestRule.onNodeWithText("-").performClick()
    }

    @Test
    fun testAnnotatedStringIsDisplayedCorrectly() {
        composeTestRule.onNodeWithText("USD x BRL").assertExists()
    }
}