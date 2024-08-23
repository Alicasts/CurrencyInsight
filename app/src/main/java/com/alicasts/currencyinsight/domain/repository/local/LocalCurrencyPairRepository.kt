package com.alicasts.currencyinsight.domain.repository.local

import com.alicasts.currencyinsight.data.database.entities.CurrencyComparisonEntity
import com.alicasts.currencyinsight.data.database.entities.CurrencyHistoricalDataEntity
import com.alicasts.currencyinsight.data.database.entities.CurrencyPairListEntity
import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel

interface LocalCurrencyPairRepository {
    suspend fun getLastFetchDate(): Long
    suspend fun getCurrencyPairsList(): List<CurrencyPairListEntity>
    suspend fun getLocalCurrencyComparisonWithDetails(comparisonCode: String, days: Int): CurrencyComparisonDetails?
    suspend fun insertCurrencyComparison(comparisonEntity: CurrencyComparisonEntity)
    suspend fun insertHistoricalData(historicalDataEntities: List<CurrencyHistoricalDataEntity>)
    suspend fun getCurrencyPairsModelList(): List<CurrencyPairListItemModel>
    suspend fun persistComparisonDetails(detailDto: CurrencyComparisonDetailDto)
    suspend fun persistUpdatedList(currencyPairListDto: List<CurrencyPairListItemDto>)
}