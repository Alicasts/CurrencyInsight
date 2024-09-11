package com.alicasts.currencyinsight.presentation.currency_pair_list

import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel

data class CurrencyPairListState(
    val isLoading: Boolean = false,
    val currencyPairList: List<CurrencyPairListItemModel> = emptyList(),
    val error: String =""
)
