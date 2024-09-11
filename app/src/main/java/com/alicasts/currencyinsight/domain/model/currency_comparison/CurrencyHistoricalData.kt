package com.alicasts.currencyinsight.domain.model.currency_comparison

data class CurrencyHistoricalData(
    val high: String,
    val low: String,
    val varBid: String,
    val pctChange: String,
    val bid: String,
    val ask: String,
    val timestamp: String
)
