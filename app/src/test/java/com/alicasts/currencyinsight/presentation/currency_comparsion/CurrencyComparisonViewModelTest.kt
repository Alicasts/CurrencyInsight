package com.alicasts.currencyinsight.presentation.currency_comparsion

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.data.mockData.CurrencyComparisonWithDetailsTestMockData
import com.alicasts.currencyinsight.domain.use_cases.get_currency_comparsion_details.GetCurrencyComparisonDetailsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class CurrencyComparisonViewModelTest {

    @get:Rule
    var instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CurrencyComparisonViewModel
    private lateinit var getCurrencyComparisonDetailsUseCase: GetCurrencyComparisonDetailsUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCurrencyComparisonDetailsUseCase = mockk(relaxed = true)
        viewModel = CurrencyComparisonViewModel(getCurrencyComparisonDetailsUseCase, SavedStateHandle().apply {
            set("currency_pair_id", "USD_BRL")
        })
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load comparison details successfully and update state`() = runTest {
        val mockComparisonDetails = CurrencyComparisonWithDetailsTestMockData.returnMockCurrencyComparisonDetails()

        coEvery { getCurrencyComparisonDetailsUseCase("USD_BRL", 15) } returns flow {
            emit(Resource.Success(mockComparisonDetails))
        }

        val observer = mockk<Observer<CurrencyComparisonState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.getCurrencyComparisonDetails("USD_BRL", 15)

        verify {
            observer.onChanged(CurrencyComparisonState(comparisonDetails = mockComparisonDetails))
        }
    }

    @Test
    fun `should emit error state when comparison details loading fails`() = runTest {
        val errorMessage = "An unexpected error occurred."

        coEvery { getCurrencyComparisonDetailsUseCase("USD_BRL", 15) } returns flow {
            emit(Resource.Error(errorMessage))
        }

        val observer = mockk<Observer<CurrencyComparisonState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.getCurrencyComparisonDetails("USD_BRL", 15)

        verify {
            observer.onChanged(CurrencyComparisonState(error = errorMessage))
        }
    }

    @Test
    fun `should set loading state when fetching comparison details`() = runTest {
        coEvery { getCurrencyComparisonDetailsUseCase("USD_BRL", 15) } returns flow {
            emit(Resource.Loading())
        }

        val observer = mockk<Observer<CurrencyComparisonState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.getCurrencyComparisonDetails("USD_BRL", 15)

        verify {
            observer.onChanged(CurrencyComparisonState(isLoading = true))
        }
    }
}