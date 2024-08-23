package com.alicasts.currencyinsight.domain.repository.remote

import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel

interface RemoteCurrencyPairRepository {
    suspend fun getCurrencyPairsList(): List<CurrencyPairListItemDto>
    suspend fun getRemoteCurrencyComparisonWithDetails(currencyPairId: String, days: Int): CurrencyComparisonDetailDto
    suspend fun convertDtoToModel(currencyPairListDto: List<CurrencyPairListItemDto>): List<CurrencyPairListItemModel>
}