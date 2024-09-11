package com.alicasts.currencyinsight.data.dto

data class CurrencyComparisonDetailDto(
    val code: String,
    val codein: String,
    val name: String,
    val high: String,
    val low: String,
    val varBid: String,
    val pctChange: String,
    val bid: String,
    val ask: String,
    val timestamp: String,
    val createDate: String,
    val historicalData: List<CurrencyHistoricalDataDto>
)
