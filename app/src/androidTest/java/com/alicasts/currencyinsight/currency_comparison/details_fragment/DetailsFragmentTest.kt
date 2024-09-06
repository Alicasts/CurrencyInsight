package com.alicasts.currencyinsight.currency_comparison.details_fragment

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alicasts.currencyinsight.R
import com.alicasts.currencyinsight.presentation.currency_comparison.CurrencyComparisonActivity
import com.alicasts.currencyinsight.presentation.currency_comparison.details_fragment.DetailsFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var activityScenario: ActivityScenario<CurrencyComparisonActivity>

    @Before
    fun setUp() {
        hiltRule.inject()

        activityScenario = ActivityScenario.launch(CurrencyComparisonActivity::class.java)
    }

    @Test
    fun testCardGeneration() {
        activityScenario.onActivity { activity ->
            val fragment = DetailsFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.cards_fragment_container, fragment)
                .commitNow()

            val card = fragment.generateCard(
                timestamp = 1650000000L,
                pctChange = "1.0",
                bid = "5.50",
                ask = "5.60",
                varBid = "0.10",
                high = "5.70",
                low = "5.30"
            )

            assertEquals("Date(DD/MM):", card[0].first)
            assertEquals("1.0", card[1].second)
            assertEquals("Bid:", card[2].first)
            assertEquals("5.50", card[2].second)
        }
    }
}