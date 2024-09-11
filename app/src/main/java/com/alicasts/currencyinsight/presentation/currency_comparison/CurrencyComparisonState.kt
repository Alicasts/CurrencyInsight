package com.alicasts.currencyinsight.presentation.currency_comparison

import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyComparisonDetails

data class CurrencyComparisonState(
    val isLoading: Boolean = false,
    val comparisonDetails: CurrencyComparisonDetails? = null,
    val error: String = ""
)
