package com.alicasts.currencyinsight.domain.repository

import com.alicasts.currencyinsight.data.database.CurrencyPairEntity
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto

interface CurrencyPairRepository {
    suspend fun getCurrencyPairList(): List<CurrencyPairListItemDto>
    suspend fun updateLastFetchDate()
    suspend fun getLastFetchDate(): Long
    suspend fun saveCurrencyPairsToDatabase(currencyPairs: List<CurrencyPairListItemDto>)
    suspend fun getCurrencyPairsFromDatabase(): List<CurrencyPairEntity>
}