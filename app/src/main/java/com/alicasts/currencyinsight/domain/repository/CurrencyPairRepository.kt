package com.alicasts.currencyinsight.domain.repository

import com.alicasts.currencyinsight.data.database.comparison.CurrencyComparisonEntity
import com.alicasts.currencyinsight.data.database.comparison.CurrencyHistoricalDataEntity
import com.alicasts.currencyinsight.data.database.list.CurrencyPairListEntity
import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails

interface CurrencyPairRepository {
    suspend fun getRemoteCurrencyPairList(): List<CurrencyPairListItemDto>
    suspend fun updateLastFetchDate()
    suspend fun getLastFetchDate(): Long
    suspend fun saveCurrencyPairsToDatabase(currencyPairs: List<CurrencyPairListItemDto>)
    suspend fun getLocalCurrencyPairsList(): List<CurrencyPairListEntity>
    suspend fun getRemoteCurrencyComparisonWithDetails(currencyPairId: String, days: Int): CurrencyComparisonDetailDto
    suspend fun getLocalCurrencyComparisonWithDetails(comparisonCode: String, days: Int):
            CurrencyComparisonDetails?
    suspend fun insertCurrencyComparison(comparisonEntity: CurrencyComparisonEntity)
    suspend fun insertHistoricalData(historicalDataEntities: List<CurrencyHistoricalDataEntity>)
}