package com.alicasts.currencyinsight.presentation.currency_pair_list

import org.junit.Rule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel
import com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list.GetCurrencyPairListUseCase
import io.mockk.mockk
import io.mockk.coEvery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import androidx.lifecycle.Observer
import com.alicasts.currencyinsight.common.Resource
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CurrencyPairListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CurrencyPairListViewModel
    private lateinit var getCurrencyPairListUseCase: GetCurrencyPairListUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCurrencyPairListUseCase = mockk(relaxed = true)
        viewModel = CurrencyPairListViewModel(getCurrencyPairListUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load data successfully and update state`() = runTest {
        val mockCurrencyPairs = listOf(
            CurrencyPairListItemModel("USD-BRL", "Dólar Americano/Real Brasileiro"),
            CurrencyPairListItemModel("EUR-BRL", "Euro/Real Brasileiro")
        )
        coEvery { getCurrencyPairListUseCase() } returns flow {
            emit(Resource.Success(mockCurrencyPairs))
        }

        val observer = mockk<Observer<CurrencyPairListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.getCurrencyPairList()

        verify {
            observer.onChanged(CurrencyPairListState(currencyPairList = mockCurrencyPairs))
        }
    }
    @Test
    fun `should emit error state when data loading fails`() = runTest {
        val errorMessage = "An unexpected error occurred."
        coEvery { getCurrencyPairListUseCase() } returns flow {
            emit(Resource.Error(errorMessage))
        }

        val observer = mockk<Observer<CurrencyPairListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.getCurrencyPairList()

        verify {
            observer.onChanged(CurrencyPairListState(error = errorMessage))
        }
    }

    @Test
    fun `should filter currency pair list based on query`() = runTest {
        val mockCurrencyPairs = listOf(
            CurrencyPairListItemModel("USD-BRL", "Dólar Americano/Real Brasileiro"),
            CurrencyPairListItemModel("EUR-BRL", "Euro/Real Brasileiro")
        )

        coEvery { getCurrencyPairListUseCase() } returns flow {
            emit(Resource.Success(mockCurrencyPairs))
        }

        val observer = mockk<Observer<CurrencyPairListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.getCurrencyPairList()

        viewModel.filterCurrencyPairList("usd")

        verify {
            observer.onChanged(CurrencyPairListState(currencyPairList = listOf(mockCurrencyPairs[0])))
        }

        viewModel.state.removeObserver(observer)
    }
}