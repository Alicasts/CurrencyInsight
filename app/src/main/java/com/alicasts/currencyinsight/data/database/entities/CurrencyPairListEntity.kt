package com.alicasts.currencyinsight.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_pairs")
data class CurrencyPairListEntity(
    @PrimaryKey val currencyPairAbbreviations: String,
    val currencyPairFullNames: String
)