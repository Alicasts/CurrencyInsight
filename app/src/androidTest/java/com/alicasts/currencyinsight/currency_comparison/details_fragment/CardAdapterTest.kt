package com.alicasts.currencyinsight.currency_comparison.details_fragment

import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alicasts.currencyinsight.presentation.currency_comparison.CurrencyComparisonActivity
import com.alicasts.currencyinsight.presentation.currency_comparison.details_fragment.CardAdapter
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
class CardAdapterTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<CurrencyComparisonActivity>
    private lateinit var adapter: CardAdapter
    private lateinit var cards: List<List<Pair<String, String>>>

    @Before
    fun setUp() {
        hiltRule.inject()

        cards = listOf(
            listOf(Pair("Key1", "Value1"), Pair("Key2", "Value2")),
            listOf(Pair("Key3", "Value3"), Pair("Key4", "Value4"))
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
            adapter = CardAdapter(cards)
            assertEquals(2, adapter.itemCount)
        }
    }

    @Test
    fun testOnCreateViewHolderIsCalled() {
        scenario.onActivity { activity ->
            adapter = CardAdapter(cards)
            val parent = activity.findViewById<ViewGroup>(android.R.id.content)
            val viewHolder = adapter.onCreateViewHolder(parent, 0)

            assertNotNull(viewHolder)
        }
    }

    @Test
    fun testOnBindViewHolderBindsDataCorrectly() {
        scenario.onActivity { activity ->
            adapter = CardAdapter(cards)
            val parent = activity.findViewById<ViewGroup>(android.R.id.content)
            val viewHolder = adapter.onCreateViewHolder(parent, 0)

            adapter.onBindViewHolder(viewHolder, 0)

            val recyclerView = viewHolder.binding.verticalRecyclerView
            assertEquals(CardRowAdapter::class.java, recyclerView.adapter?.javaClass)
            assertEquals(2, recyclerView.adapter?.itemCount)
        }
    }
}