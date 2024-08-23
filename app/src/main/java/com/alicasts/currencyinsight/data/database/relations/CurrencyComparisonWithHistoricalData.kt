package com.alicasts.currencyinsight.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.alicasts.currencyinsight.data.database.entities.CurrencyComparisonEntity
import com.alicasts.currencyinsight.data.database.entities.CurrencyHistoricalDataEntity

data class CurrencyComparisonWithHistoricalData(
    @Embedded val comparison: CurrencyComparisonEntity,
    @Relation(
        parentColumn = "comparisonCode",
        entityColumn = "comparisonCode"
    )
    val historicalData: List<CurrencyHistoricalDataEntity>
)
