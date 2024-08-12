package com.alicasts.currencyinsight.data.remote

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinAwesomeApi {

    @GET("json/available")
    suspend fun getCurrencyPairList(): JsonObject

}