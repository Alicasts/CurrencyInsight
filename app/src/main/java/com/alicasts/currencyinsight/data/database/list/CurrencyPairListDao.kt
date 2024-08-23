package com.alicasts.currencyinsight.data.database.list

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrencyPairListDao {

    @Query("SELECT * FROM currency_pairs")
    suspend fun getAllCurrencyPairs(): List<CurrencyPairListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyPairs(currencyPairs: List<CurrencyPairListEntity>)

    @Query("DELETE FROM currency_pairs")
    suspend fun deleteAllCurrencyPairs()
}