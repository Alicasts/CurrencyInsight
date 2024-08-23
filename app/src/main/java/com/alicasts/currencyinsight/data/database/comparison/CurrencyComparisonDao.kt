package com.alicasts.currencyinsight.data.database.comparison

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface CurrencyComparisonDao {

    @Transaction
    @Query("SELECT * FROM currency_comparisons WHERE comparisonCode = :comparisonCode")
    suspend fun getCurrencyComparisonWithHistoricalData(comparisonCode: String): CurrencyComparisonWithHistoricalData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyComparison(comparison: CurrencyComparisonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoricalData(historicalData: List<CurrencyHistoricalDataEntity>)

    @Query("DELETE FROM currency_comparisons WHERE comparisonCode = :comparisonCode")
    suspend fun deleteCurrencyComparison(comparisonCode: String)

    @Query("DELETE FROM currency_historical_data WHERE comparisonCode = :comparisonCode")
    suspend fun deleteHistoricalData(comparisonCode: String)
}