package com.alicasts.currencyinsight.data.database.comparison

import androidx.room.Embedded
import androidx.room.Relation

data class CurrencyComparisonWithHistoricalData(
    @Embedded val comparison: CurrencyComparisonEntity,
    @Relation(
        parentColumn = "comparisonCode",
        entityColumn = "comparisonCode"
    )
    val historicalData: List<CurrencyHistoricalDataEntity>
)
