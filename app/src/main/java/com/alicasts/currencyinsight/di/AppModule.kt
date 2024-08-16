package com.alicasts.currencyinsight.di

import com.alicasts.currencyinsight.common.Constants
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import com.alicasts.currencyinsight.data.remote.repository.CurrencyPairRepositoryImpl
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideCurrencyPairRepository(api: CoinAwesomeApi): CurrencyPairRepository {
        return CurrencyPairRepositoryImpl(api)
    }
}