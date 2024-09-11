package com.alicasts.currencyinsight.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.alicasts.currencyinsight.data.database.entities.CurrencyComparisonEntity
import com.alicasts.currencyinsight.data.database.relations.CurrencyComparisonWithHistoricalData
import com.alicasts.currencyinsight.data.database.entities.CurrencyHistoricalDataEntity

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