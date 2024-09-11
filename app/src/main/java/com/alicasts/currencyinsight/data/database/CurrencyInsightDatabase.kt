package com.alicasts.currencyinsight.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alicasts.currencyinsight.data.database.dao.CurrencyComparisonDao
import com.alicasts.currencyinsight.data.database.entities.CurrencyComparisonEntity
import com.alicasts.currencyinsight.data.database.entities.CurrencyHistoricalDataEntity
import com.alicasts.currencyinsight.data.database.dao.CurrencyPairListDao
import com.alicasts.currencyinsight.data.database.entities.CurrencyPairListEntity

@Database(entities = [CurrencyPairListEntity::class, CurrencyComparisonEntity::class,
    CurrencyHistoricalDataEntity::class], version = 2)
abstract class CurrencyInsightDatabase : RoomDatabase() {
    abstract fun currencyPairListDao(): CurrencyPairListDao
    abstract fun currencyComparisonDao(): CurrencyComparisonDao
}