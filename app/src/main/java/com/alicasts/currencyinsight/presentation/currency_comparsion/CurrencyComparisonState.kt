package com.alicasts.currencyinsight.presentation.currency_comparsion

import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails

data class CurrencyComparisonState(
    val isLoading: Boolean = false,
    val comparisonDetails: CurrencyComparisonDetails? = null,
    val error: String = ""
)
