package com.alicasts.currencyinsight.data.dto

import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel

data class CurrencyPairListItemDto(
    val currencyPairAbbreviations: String,
    val currencyPairFullNames: String
) {
    fun toCurrencyPairListItemModel(): CurrencyPairListItemModel {
        return CurrencyPairListItemModel(
            currencyPairAbbreviations = currencyPairAbbreviations,
            currencyPairFullNames = currencyPairFullNames
        )
    }
}