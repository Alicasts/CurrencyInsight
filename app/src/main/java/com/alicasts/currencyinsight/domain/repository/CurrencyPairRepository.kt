package com.alicasts.currencyinsight.domain.repository

import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto


interface CurrencyPairRepository {

    suspend fun getCurrencyPairList(): List<CurrencyPairListItemDto>

}