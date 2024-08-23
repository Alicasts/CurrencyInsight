package com.alicasts.currencyinsight.data.remote.repository

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.alicasts.currencyinsight.common.Constants.LAST_FETCH_DATE_KEY
import com.alicasts.currencyinsight.data.database.comparison.CurrencyComparisonDao
import com.alicasts.currencyinsight.data.database.comparison.CurrencyComparisonEntity
import com.alicasts.currencyinsight.data.database.comparison.CurrencyComparisonWithHistoricalData
import com.alicasts.currencyinsight.data.database.comparison.CurrencyHistoricalDataEntity
import com.alicasts.currencyinsight.data.database.list.CurrencyPairListDao
import com.alicasts.currencyinsight.data.database.list.CurrencyPairListEntity
import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.data.dto.CurrencyHistoricalDataDto
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.mappers.CurrencyComparisonMapper
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import javax.inject.Inject

class CurrencyPairRepositoryImpl @Inject constructor(
    private val api: CoinAwesomeApi,
    private val currencyPairListDao: CurrencyPairListDao,
    private val sharedPreferences: SharedPreferences,
    private val currencyPairMapper: CurrencyPairMapper,
    private val currencyComparisonDao: CurrencyComparisonDao,
    private val currencyComparisonMapper: CurrencyComparisonMapper
) : CurrencyPairRepository {

    override suspend fun getRemoteCurrencyPairList(): List<CurrencyPairListItemDto> {
        val response = api.getCurrencyPairList()
        return currencyPairMapper.parseCurrencyPairListResponse(response)
    }

    override suspend fun updateLastFetchDate() {
        val currentTime = System.currentTimeMillis()
        sharedPreferences.edit().putLong(LAST_FETCH_DATE_KEY, currentTime).apply()
    }

    override suspend fun getLastFetchDate(): Long {
        return sharedPreferences.getLong(LAST_FETCH_DATE_KEY, 0L)
    }

    override suspend fun saveCurrencyPairsToDatabase(currencyPairs: List<CurrencyPairListItemDto>) {
        val entities = currencyPairMapper.fromDtoToEntityList(currencyPairs)
        currencyPairListDao.insertCurrencyPairs(entities)
    }

    override suspend fun getLocalCurrencyPairsList(): List<CurrencyPairListEntity> {
        return currencyPairListDao.getAllCurrencyPairs()
    }

    override suspend fun getRemoteCurrencyComparisonWithDetails(currencyPairId: String, days: Int): CurrencyComparisonDetailDto {
        val response = api.getCurrencyComparisonWithDetails(currencyPairId, days)
        return currencyComparisonMapper.parseCurrencyComparisonDetailsResponse(response)
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
}