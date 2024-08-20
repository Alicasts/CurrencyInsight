package com.alicasts.currencyinsight.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_pairs")
data class CurrencyPairEntity(

    @PrimaryKey val id: String,
    val currencyPairAbbreviations: String,
    val currencyPairFullNames: String
)