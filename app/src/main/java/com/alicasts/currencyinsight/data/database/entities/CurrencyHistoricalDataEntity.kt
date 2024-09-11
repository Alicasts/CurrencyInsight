package com.alicasts.currencyinsight.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_historical_data")
data class CurrencyHistoricalDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val comparisonCode: String,
    val high: String,
    val low: String,
    val varBid: String,
    val pctChange: String,
    val bid: String,
    val ask: String,
    val timestamp: String
)
