package com.alicasts.currencyinsight.domain.repository

import com.alicasts.currencyinsight.data.database.CurrencyPairEntity
import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails

interface CurrencyPairRepository {
    suspend fun getCurrencyPairList(): List<CurrencyPairListItemDto>
    suspend fun updateLastFetchDate()
    suspend fun getLastFetchDate(): Long
    suspend fun saveCurrencyPairsToDatabase(currencyPairs: List<CurrencyPairListItemDto>)
    suspend fun getCurrencyPairsFromDatabase(): List<CurrencyPairEntity>
    suspend fun getCurrencyComparisonDetails(currencyPairId: String, num: Int): CurrencyComparisonDetailDto
}