package com.alicasts.currencyinsight.presentation.currency_pair_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel
import com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list.GetCurrencyPairListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyPairListViewModel @Inject constructor(
    private val getCurrencyPairListUseCase: GetCurrencyPairListUseCase
) : ViewModel() {

    private val _state = MutableLiveData<CurrencyPairListState>()
    val state: LiveData<CurrencyPairListState> = _state

    init {
        getCurrencyPairList()
    }

    private fun getCurrencyPairList() {
        viewModelScope.launch {
            getCurrencyPairListUseCase().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.postValue(CurrencyPairListState(
                            currencyPairList = result.data ?: emptyList()))
                    }
                    is Resource.Error -> {
                        _state.postValue(CurrencyPairListState(
                            error = result.message ?: "An unexpected error occurred."))
                    }
                    is Resource.Loading -> {
                        _state.postValue(CurrencyPairListState(isLoading = true))
                    }
                }
            }.launchIn(this)
        }
    }
}