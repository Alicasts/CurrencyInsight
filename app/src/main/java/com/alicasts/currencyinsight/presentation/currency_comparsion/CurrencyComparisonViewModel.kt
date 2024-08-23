package com.alicasts.currencyinsight.presentation.currency_comparsion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.domain.use_cases.get_currency_comparsion_details.GetCurrencyComparisonDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyComparisonViewModel @Inject constructor(
    private val getCurrencyComparisonDetailsUseCase: GetCurrencyComparisonDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableLiveData<CurrencyComparisonState>()
    val state: LiveData<CurrencyComparisonState> = _state

    init {
        val currencyPairId = savedStateHandle.get<String>("currency_pair_id")
        getCurrencyComparisonDetails(currencyPairId!!)
    }

    fun getCurrencyComparisonDetails(currencyPairId: String, num: Int = 15) {
        viewModelScope.launch {
            getCurrencyComparisonDetailsUseCase(currencyPairId, num).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.postValue(
                            CurrencyComparisonState(comparisonDetails = result.data)
                        )
                    }
                    is Resource.Error -> {
                        _state.postValue(
                            CurrencyComparisonState(
                                error = result.message ?: "An unexpected error occurred."
                            )
                        )
                    }
                    is Resource.Loading -> {
                        _state.postValue(CurrencyComparisonState(isLoading = true))
                    }
                }
            }.launchIn(this)
        }
    }
}
