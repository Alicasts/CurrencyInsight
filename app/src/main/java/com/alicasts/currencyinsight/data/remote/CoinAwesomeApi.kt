package com.alicasts.currencyinsight.data.remote

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinAwesomeApi {

    @GET("json/available")
    suspend fun getCurrencyPairList(): JsonObject

    @GET("json/daily/{currencyPairId}/{num}")
    suspend fun getCurrencyComparisonWithDetails(
        @Path("currencyPairId") currencyPairId: String,
        @Path("num") num: Int
    ): JsonArray

}