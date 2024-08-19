package com.alicasts.currencyinsight.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrencyPairEntity::class], version = 1)
abstract class CurrencyInsightDatabase : RoomDatabase() {
    abstract fun currencyPairDao(): CurrencyPairDao
}