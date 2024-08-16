package com.alicasts.currencyinsight.common

import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.google.gson.JsonObject

fun parseCurrencyPairListResponse(response: JsonObject): List<CurrencyPairListItemDto> {
    val currencyPairList = response.entrySet().map { (key, value) ->
        CurrencyPairListItemDto(key, value.asString)
    }

    return currencyPairList
}