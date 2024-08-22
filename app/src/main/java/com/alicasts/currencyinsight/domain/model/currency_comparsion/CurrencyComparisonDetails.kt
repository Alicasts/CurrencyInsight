package com.alicasts.currencyinsight.domain.model.currency_comparsion

data class CurrencyComparisonDetails(
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
    val historicalData: List<CurrencyHistoricalData> = emptyList()
)
