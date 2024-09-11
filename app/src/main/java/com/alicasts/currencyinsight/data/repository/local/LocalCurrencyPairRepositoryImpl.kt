package com.alicasts.currencyinsight.data.repository.local

import android.content.SharedPreferences
import com.alicasts.currencyinsight.common.Constants.LAST_FETCH_DATE_KEY
import com.alicasts.currencyinsight.data.database.dao.CurrencyComparisonDao
import com.alicasts.currencyinsight.data.database.dao.CurrencyPairListDao
import com.alicasts.currencyinsight.data.database.entities.CurrencyComparisonEntity
import com.alicasts.currencyinsight.data.database.entities.CurrencyHistoricalDataEntity
import com.alicasts.currencyinsight.data.database.entities.CurrencyPairListEntity
import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.data.dto.CurrencyHistoricalDataDto
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.mappers.CurrencyComparisonMapper
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel
import com.alicasts.currencyinsight.domain.repository.local.LocalCurrencyPairRepository
import javax.inject.Inject

class LocalCurrencyPairRepositoryImpl @Inject constructor(
    private val currencyPairListDao: CurrencyPairListDao,
    private val sharedPreferences: SharedPreferences,
    private val currencyPairMapper: CurrencyPairMapper,
    private val currencyComparisonDao: CurrencyComparisonDao,
    private val currencyComparisonMapper: CurrencyComparisonMapper
) : LocalCurrencyPairRepository {

    private fun updateLastFetchDate() {
        val currentTime = System.currentTimeMillis()
        sharedPreferences.edit().putLong(LAST_FETCH_DATE_KEY, currentTime).apply()
    }

    override suspend fun getLastFetchDate(): Long {
        return sharedPreferences.getLong(LAST_FETCH_DATE_KEY, 0L)
    }

    private suspend fun saveCurrencyPairsToDatabase(currencyPairs: List<CurrencyPairListItemDto>) {
        val entities = currencyPairMapper.fromDtoToEntityList(currencyPairs)
        currencyPairListDao.insertCurrencyPairs(entities)
    }

    override suspend fun getCurrencyPairsList(): List<CurrencyPairListEntity> {
        return currencyPairListDao.getAllCurrencyPairs()
    }

    override suspend fun getLocalCurrencyComparisonWithDetails(
        comparisonCode: String, days: Int
    ): CurrencyComparisonDetails? {

        val localData = currencyComparisonDao.getCurrencyComparisonWithHistoricalData(comparisonCode) ?: return null

        return currencyComparisonMapper.mapToCurrencyComparisonDetails(localData)
    }

    override suspend fun insertCurrencyComparison(comparisonEntity: CurrencyComparisonEntity) {
        currencyComparisonDao.insertCurrencyComparison(comparisonEntity)
    }

    override suspend fun insertHistoricalData(historicalDataEntities: List<CurrencyHistoricalDataEntity>) {
        currencyComparisonDao.insertHistoricalData(historicalDataEntities)
    }

    override suspend fun getCurrencyPairsModelList(): List<CurrencyPairListItemModel> {
        val entities = currencyPairListDao.getAllCurrencyPairs()
        return currencyPairMapper.fromEntityToModelList(entities)
    }

    override suspend fun persistComparisonDetails(detailDto: CurrencyComparisonDetailDto) {
        val comparisonEntity = currencyComparisonMapper.mapToEntity(detailDto)
        val historicalDataDtos: List<CurrencyHistoricalDataDto> = detailDto.historicalData
        val historicalDataEntities = currencyComparisonMapper.mapToHistoricalEntities(historicalDataDtos, comparisonEntity.comparisonCode)

        insertCurrencyComparison(comparisonEntity)
        clearSelectedHistoricalData(comparisonEntity.comparisonCode)
        insertHistoricalData(historicalDataEntities)
    }

    override suspend fun persistUpdatedList(currencyPairListDto: List<CurrencyPairListItemDto>) {
        updateLastFetchDate()
        saveCurrencyPairsToDatabase(currencyPairListDto)
    }

    override suspend fun clearSelectedHistoricalData(comparisonCode: String) {
        currencyComparisonDao.deleteHistoricalData(comparisonCode)
    }
}