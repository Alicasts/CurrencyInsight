package com.alicasts.currencyinsight.data.remote.repository

import android.content.SharedPreferences
import com.alicasts.currencyinsight.common.Constants.LAST_FETCH_DATE_KEY
import com.alicasts.currencyinsight.data.database.CurrencyPairDao
import com.alicasts.currencyinsight.data.database.CurrencyPairEntity
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import com.google.gson.JsonObject
import javax.inject.Inject

class CurrencyPairRepositoryImpl @Inject constructor(
    private val api: CoinAwesomeApi,
    private val currencyPairDao: CurrencyPairDao,
    private val sharedPreferences: SharedPreferences,
    private val currencyPairMapper: CurrencyPairMapper
) : CurrencyPairRepository {

    override suspend fun getCurrencyPairList(): List<CurrencyPairListItemDto> {
        val response = api.getCurrencyPairList()
        return parseCurrencyPairListResponse(response)
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
        currencyPairDao.insertCurrencyPairs(entities)
    }

    override suspend fun getCurrencyPairsFromDatabase(): List<CurrencyPairEntity> {
        return currencyPairDao.getAllCurrencyPairs()
    }

    private fun parseCurrencyPairListResponse(response: JsonObject): List<CurrencyPairListItemDto> {
        val currencyPairList = response.entrySet().map { (key, value) ->
            CurrencyPairListItemDto(key, value.asString)
        }
        return currencyPairList
    }
}