package com.alicasts.currencyinsight.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.alicasts.currencyinsight.common.Constants
import com.alicasts.currencyinsight.data.database.CurrencyInsightDatabase
import com.alicasts.currencyinsight.data.database.dao.CurrencyComparisonDao
import com.alicasts.currencyinsight.data.database.dao.CurrencyPairListDao
import com.alicasts.currencyinsight.data.mappers.CurrencyComparisonMapper
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import com.alicasts.currencyinsight.data.repository.local.LocalCurrencyPairRepositoryImpl
import com.alicasts.currencyinsight.data.repository.remote.RemoteCurrencyPairRepositoryImpl
import com.alicasts.currencyinsight.domain.repository.local.LocalCurrencyPairRepository
import com.alicasts.currencyinsight.domain.repository.remote.RemoteCurrencyPairRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideCoinAwesomeApi(): CoinAwesomeApi {
        val gson: Gson = GsonBuilder().create()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CoinAwesomeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteCurrencyPairRepository(
        api: CoinAwesomeApi,
        currencyPairMapper: CurrencyPairMapper,
        currencyComparisonMapper: CurrencyComparisonMapper
    ): RemoteCurrencyPairRepository {
        return RemoteCurrencyPairRepositoryImpl(
            api = api,
            currencyPairMapper = currencyPairMapper,
            currencyComparisonMapper = currencyComparisonMapper
        )
    }

    @Provides
    @Singleton
    fun provideLocalCurrencyPairRepository(
        currencyPairListDao: CurrencyPairListDao,
        sharedPreferences: SharedPreferences,
        currencyPairMapper: CurrencyPairMapper,
        currencyComparisonDao: CurrencyComparisonDao,
        currencyComparisonMapper: CurrencyComparisonMapper
    ): LocalCurrencyPairRepository {
        return LocalCurrencyPairRepositoryImpl(
            currencyPairListDao = currencyPairListDao,
            sharedPreferences = sharedPreferences,
            currencyPairMapper = currencyPairMapper,
            currencyComparisonDao = currencyComparisonDao,
            currencyComparisonMapper = currencyComparisonMapper
        )
    }

    @Provides
    @Singleton
    fun provideDatabase(
        context: Context
    ): CurrencyInsightDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            CurrencyInsightDatabase::class.java,
            "currency_insight_database"
        ).build()
    }

    @Provides
    fun provideCurrencyPairListDao(database: CurrencyInsightDatabase): CurrencyPairListDao {
        return database.currencyPairListDao()
    }

    @Provides
    fun provideCurrencyComparisonDao(database: CurrencyInsightDatabase): CurrencyComparisonDao {
        return database.currencyComparisonDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("currency_insight_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideCurrencyPairMapper(): CurrencyPairMapper {
        return CurrencyPairMapper()
    }

    @Provides
    @Singleton
    fun provideCurrencyComparisonMapper(): CurrencyComparisonMapper {
        return CurrencyComparisonMapper()
    }
}