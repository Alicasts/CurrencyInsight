package com.alicasts.currencyinsight.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrencyPairDao {

    @Query("SELECT * FROM currency_pairs")
    suspend fun getAllCurrencyPairs(): List<CurrencyPairEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyPairs(currencyPairs: List<CurrencyPairEntity>)

    @Query("DELETE FROM currency_pairs")
    suspend fun deleteAllCurrencyPairs()
}