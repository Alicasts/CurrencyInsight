package com.alicasts.currencyinsight.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import com.alicasts.currencyinsight.common.Constants
import com.alicasts.currencyinsight.data.database.CurrencyInsightDatabase
import com.alicasts.currencyinsight.data.database.CurrencyPairDao
import com.alicasts.currencyinsight.data.mappers.CurrencyComparisonMapper
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import com.alicasts.currencyinsight.data.remote.repository.CurrencyPairRepositoryImpl
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
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
    fun provideCurrencyPairRepository(
        api: CoinAwesomeApi,
        dao: CurrencyPairDao,
        sharedPreferences: SharedPreferences,
        mapper: CurrencyPairMapper
    ): CurrencyPairRepository {
        return CurrencyPairRepositoryImpl(api, dao, sharedPreferences, mapper)
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
    fun provideCurrencyPairDao(database: CurrencyInsightDatabase): CurrencyPairDao {
        return database.currencyPairDao()
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