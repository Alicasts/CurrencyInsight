package com.alicasts.currencyinsight.presentation.currency_pair_list

import org.junit.Rule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel
import com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list.GetCurrencyPairListUseCase
import io.mockk.mockk
import io.mockk.coEvery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import io.mockk.clearMocks
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class CurrencyPairListViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    private lateinit var viewModel: CurrencyPairListViewModel
    private var getCurrencyPairListUseCase: GetCurrencyPairListUseCase = mockk()
    private var repository: CurrencyPairRepository = mockk(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        clearMocks(getCurrencyPairListUseCase, repository)
        getCurrencyPairListUseCase = GetCurrencyPairListUseCase(repository)
        viewModel = CurrencyPairListViewModel(getCurrencyPairListUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test error is handled correctly`() = runTest {
        val errorMessage = "Couldn't reach server."
        coEvery { repository.getCurrencyPairList() } throws IOException(errorMessage)

        viewModel.getCurrencyPairList()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals(errorMessage, state.error)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `test currency pair list loaded successfully`() = runTest {
        val currencyPairs = listOf(
            CurrencyPairListItemModel("USD/EUR", "US Dollar/Euro"),
            CurrencyPairListItemModel("GBP/USD", "British Pound/US Dollar")
        )
        coEvery { repository.getCurrencyPairList() } coAnswers {
            listOf(
                CurrencyPairListItemDto("USD/EUR", "US Dollar/Euro"),
                CurrencyPairListItemDto("GBP/USD", "British Pound/US Dollar")
            )
        }

        viewModel.getCurrencyPairList()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals(currencyPairs, state.currencyPairList)
        assertEquals(false, state.isLoading)
    }

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)

        try {
            afterObserve.invoke()

            if (!latch.await(time, timeUnit)) {
                throw RuntimeException("LiveData value was never set.")
            }
        } finally {
            this.removeObserver(observer)
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}