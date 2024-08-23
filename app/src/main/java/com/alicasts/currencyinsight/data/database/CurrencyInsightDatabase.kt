package com.alicasts.currencyinsight.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alicasts.currencyinsight.data.database.comparison.CurrencyComparisonDao
import com.alicasts.currencyinsight.data.database.comparison.CurrencyComparisonEntity
import com.alicasts.currencyinsight.data.database.comparison.CurrencyHistoricalDataEntity
import com.alicasts.currencyinsight.data.database.list.CurrencyPairListDao
import com.alicasts.currencyinsight.data.database.list.CurrencyPairListEntity

@Database(entities = [CurrencyPairListEntity::class, CurrencyComparisonEntity::class,
    CurrencyHistoricalDataEntity::class], version = 2)
abstract class CurrencyInsightDatabase : RoomDatabase() {
    abstract fun CurrencyPairListDao(): CurrencyPairListDao
    abstract fun currencyComparisonDao(): CurrencyComparisonDao
}