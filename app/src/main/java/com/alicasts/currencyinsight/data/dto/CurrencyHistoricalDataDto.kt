package com.alicasts.currencyinsight.data.dto

data class CurrencyHistoricalDataDto(
    val high: String,
    val low: String,
    val varBid: String,
    val pctChange: String,
    val bid: String,
    val ask: String,
    val timestamp: String
)
