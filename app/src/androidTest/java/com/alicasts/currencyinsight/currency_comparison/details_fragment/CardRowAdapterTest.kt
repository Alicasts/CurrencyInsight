package com.alicasts.currencyinsight.currency_comparison.details_fragment

import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alicasts.currencyinsight.presentation.currency_comparison.CurrencyComparisonActivity
import com.alicasts.currencyinsight.presentation.currency_comparison.details_fragment.CardRowAdapter
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class CardRowAdapterTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<CurrencyComparisonActivity>
    private lateinit var adapter: CardRowAdapter
    private lateinit var rows: List<Pair<String, String>>

    @Before
    fun setUp() {
        hiltRule.inject()

        rows = listOf(
            Pair("Row 1 Key", "Row 1 Value"),
            Pair("Row 2 Key", "Row 2 Value")
        )

        scenario = ActivityScenario.launch(CurrencyComparisonActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testItemCountIsCorrect() {
        scenario.onActivity {
            adapter = CardRowAdapter(rows)
            assertEquals(2, adapter.itemCount)
        }
    }

    @Test
    fun testOnCreateViewHolderIsCalled() {
        scenario.onActivity { activity ->
            adapter = CardRowAdapter(rows)
            val parent = activity.findViewById<ViewGroup>(android.R.id.content)
            val viewHolder = adapter.onCreateViewHolder(parent, 0)

            assertNotNull(viewHolder)

            assertNotNull(viewHolder.binding.root)
        }
    }

    @Test
    fun testOnBindViewHolderBindsDataCorrectly() {
        scenario.onActivity { activity ->
            adapter = CardRowAdapter(rows)
            val parent = activity.findViewById<ViewGroup>(android.R.id.content)
            val viewHolder = adapter.onCreateViewHolder(parent, 0)

            adapter.onBindViewHolder(viewHolder, 0)

            assertEquals("Row 1 Key", viewHolder.binding.labelTextView.text)
            assertEquals("Row 1 Value", viewHolder.binding.valueTextView.text)
        }
    }
}