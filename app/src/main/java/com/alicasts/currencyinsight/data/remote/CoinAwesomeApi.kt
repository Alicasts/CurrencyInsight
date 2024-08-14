package com.alicasts.currencyinsight.data.remote

import com.google.gson.JsonObject
import retrofit2.http.GET

interface CoinAwesomeApi {

    @GET("json/available")
    suspend fun getCurrencyPairList(): JsonObject

}