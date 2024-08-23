package com.alicasts.currencyinsight.data.database.comparison

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto

@Entity(tableName = "currency_comparisons")
data class CurrencyComparisonEntity(
    @PrimaryKey val comparisonCode: String,
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
    val createDate: String
)
