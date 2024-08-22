package com.alicasts.currencyinsight.presentation.currency_pair_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel
import com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list.GetCurrencyPairListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.Normalizer
import javax.inject.Inject

@HiltViewModel
class CurrencyPairListViewModel @Inject constructor(
    private val getCurrencyPairListUseCase: GetCurrencyPairListUseCase
) : ViewModel() {

    private val _state = MutableLiveData<CurrencyPairListState>()
    val state: LiveData<CurrencyPairListState> = _state

    private var currentList = emptyList<CurrencyPairListItemModel>()

    init {
        getCurrencyPairList()
    }

    fun getCurrencyPairList() {
        viewModelScope.launch {
            getCurrencyPairListUseCase().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        currentList = result.data ?: emptyList()
                        _state.postValue(CurrencyPairListState(
                            currencyPairList = currentList)
                        )
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

    fun filterCurrencyPairList(query: String) {
        val normalizedQuery = normalizeString(query)
        val filteredList = if (normalizedQuery.isEmpty()) {
            currentList
        } else {
            currentList.filter {
                normalizeString(it.currencyPairAbbreviations).contains(normalizedQuery, ignoreCase = true) ||
                        normalizeString(it.currencyPairFullNames).contains(normalizedQuery, ignoreCase = true)
            }
        }
        _state.postValue(CurrencyPairListState(currencyPairList = filteredList))
    }

    private fun normalizeString(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
}