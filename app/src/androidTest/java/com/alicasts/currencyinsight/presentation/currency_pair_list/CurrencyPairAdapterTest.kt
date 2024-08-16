package com.alicasts.currencyinsight.presentation.currency_pair_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel
import com.alicasts.currencyinsight.presentation.MainActivity
import com.alicasts.currencyinsight.databinding.ItemCurrencyPairBinding
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
    private lateinit var currencyPairs: List<CurrencyPairListItemModel>

    @Before
    fun setUp() {
        hiltRule.inject()

        scenario = ActivityScenario.launch(CurrencyPairListActivity::class.java)
        currencyPairs = listOf(
            CurrencyPairListItemModel("USD/EUR", "US Dollar/Euro"),
            CurrencyPairListItemModel("GBP/USD", "British Pound/US Dollar")
        )
        adapter = CurrencyPairListAdapter(currencyPairs)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testItemCount() {
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun testBindingData() {
        scenario.use { activityScenario ->
            activityScenario.onActivity { activity ->
                val parent = activity.findViewById<ViewGroup>(android.R.id.content)

                val adapter = CurrencyPairListAdapter(
                    listOf(
                        CurrencyPairListItemModel("USD/EUR", "US Dollar/Euro"),
                        CurrencyPairListItemModel("GBP/USD", "British Pound/US Dollar")
                    )
                )

                val viewHolder = adapter.onCreateViewHolder(parent, 0)
                adapter.onBindViewHolder(viewHolder, 0)

                val itemBinding = ItemCurrencyPairBinding.bind(viewHolder.itemView)

                assertEquals("USD/EUR", itemBinding.currencyPairAbbreviationsTextView.text)
                assertEquals("US Dollar/Euro", itemBinding.currencyPairFullNamesTextView.text)
            }
        }
    }
}