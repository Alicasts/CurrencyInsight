package com.alicasts.currencyinsight.data.remote.repository

import android.content.SharedPreferences
import com.alicasts.currencyinsight.common.Constants.LAST_FETCH_DATE_KEY
import com.alicasts.currencyinsight.data.database.CurrencyPairDao
import com.alicasts.currencyinsight.data.database.CurrencyPairEntity
import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.data.dto.CurrencyHistoricalDataDto
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyHistoricalData
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import com.google.gson.JsonArray
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

    override suspend fun getCurrencyComparisonDetails(currencyPairId: String, num: Int): CurrencyComparisonDetailDto {
        val response = api.getCurrencyComparisonDetails(currencyPairId, num)
        return parseCurrencyComparisonDetailsResponse(response)
    }

    private fun parseCurrencyPairListResponse(response: JsonObject): List<CurrencyPairListItemDto> {
        val currencyPairList = response.entrySet().map { (key, value) ->
            CurrencyPairListItemDto(key, value.asString)
        }
        return currencyPairList
    }

    private fun parseCurrencyComparisonDetailsResponse(response: JsonArray): CurrencyComparisonDetailDto {

        val historicalData = response.drop(1).map { element ->
            val jsonObject = element.asJsonObject
            CurrencyHistoricalDataDto(
                high = jsonObject["high"].asString,
                low = jsonObject["low"].asString,
                varBid = jsonObject["varBid"].asString,
                pctChange = jsonObject["pctChange"].asString,
                bid = jsonObject["bid"].asString,
                ask = jsonObject["ask"].asString,
                timestamp = jsonObject["timestamp"].asString
            )
        }

        val jsonObject = response[0].asJsonObject

         return CurrencyComparisonDetailDto(
            code = jsonObject["code"]?.asString ?: "",
            codein = jsonObject["codein"]?.asString ?: "",
            name = jsonObject["name"]?.asString ?: "",
            high = jsonObject["high"].asString,
            low = jsonObject["low"].asString,
            varBid = jsonObject["varBid"].asString,
            pctChange = jsonObject["pctChange"].asString,
            bid = jsonObject["bid"].asString,
            ask = jsonObject["ask"].asString,
            timestamp = jsonObject["timestamp"].asString,
            createDate = jsonObject["create_date"]?.asString ?: "",
            historicalData = historicalData
        )
    }
}