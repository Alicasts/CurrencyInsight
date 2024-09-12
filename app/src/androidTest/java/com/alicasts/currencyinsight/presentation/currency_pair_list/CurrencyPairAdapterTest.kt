package com.alicasts.currencyinsight.presentation.currency_pair_list

import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class CurrencyPairAdapterTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<CurrencyPairListActivity>
    private lateinit var adapter: CurrencyPairListAdapter
    private lateinit var currencyPairs: MutableList<CurrencyPairListItemModel>

    @Before
    fun setUp() {
        hiltRule.inject()

        currencyPairs = mutableListOf(
            CurrencyPairListItemModel("USD/EUR", "US Dollar/Euro"),
            CurrencyPairListItemModel("GBP/USD", "British Pound/US Dollar")
        )

        scenario = ActivityScenario.launch(CurrencyPairListActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testItemClicked() {
        var clickedItem: String? = null

        scenario.onActivity { activity ->
            adapter = CurrencyPairListAdapter(currencyPairs) { abbreviation ->
                clickedItem = abbreviation
            }

            val parent = activity.findViewById<ViewGroup>(android.R.id.content)
            val viewHolder = adapter.onCreateViewHolder(parent, 0)
            adapter.onBindViewHolder(viewHolder, 0)

            viewHolder.itemView.performClick()

            assertEquals("USD/EUR", clickedItem)
        }
    }
}