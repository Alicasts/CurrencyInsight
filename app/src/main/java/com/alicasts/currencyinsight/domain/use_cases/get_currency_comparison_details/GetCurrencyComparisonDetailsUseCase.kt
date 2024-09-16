package com.alicasts.currencyinsight.domain.use_cases.get_currency_comparison_details

import android.content.SharedPreferences
import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyHistoricalData
import com.alicasts.currencyinsight.domain.repository.local.LocalCurrencyPairRepository
import com.alicasts.currencyinsight.domain.repository.remote.RemoteCurrencyPairRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetCurrencyComparisonDetailsUseCase @Inject constructor(
    private val remoteRepository: RemoteCurrencyPairRepository,
    private val localRepository: LocalCurrencyPairRepository,
    private val sharedPreferences: SharedPreferences
) {
    operator fun invoke(currencyPairId: String): Flow<Resource<CurrencyComparisonDetails>> = flow {
        try {
            emit(Resource.Loading())
            val localData = localRepository.getLocalCurrencyComparisonWithDetails(comparisonCode = currencyPairId)

            val currencyComparisonDetails = if (shouldUpdateData(localData = localData)) {
                updateCurrencyComparisonData(currencyPairId = currencyPairId)
            } else localData

            emit(Resource.Success(currencyComparisonDetails!!))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred."))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Couldn't reach server."))
        }
    }

    private fun shouldUpdateData(localData: CurrencyComparisonDetails?): Boolean {
        return localData == null ||
            isOutdated(localData.createDate) ||
            daysToFetchHasChanged(localData.historicalData)
    }

    private fun daysToFetchHasChanged(historicalData: List<CurrencyHistoricalData>) = ((historicalData.size + 1) !=
    getDaysToFetchData())


    private fun isOutdated(createDate: String): Boolean {
        val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val parsedCreateDate = dateTimeFormatter.parse(createDate) ?: return true

        val diffInHours = (Date().time - parsedCreateDate.time) / (1000 * 60 * 60)
        return diffInHours >= 4
    }

    private fun getDaysToFetchData() = sharedPreferences.getInt("days_to_fetch_data", 15)


    private suspend fun fetchAndPersistRemoteData(
        currencyPairId: String,
        daysToFetch: Int
    ): CurrencyComparisonDetails? {
        val detailDto = remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, daysToFetch)

        localRepository.apply {
            deleteCurrencyComparison(currencyPairId)
            clearSelectedHistoricalData(currencyPairId)
            persistComparisonDetails(detailDto)
        }

        return localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId)
    }

    private suspend fun updateCurrencyComparisonData(currencyPairId: String): CurrencyComparisonDetails {
        localRepository.deleteCurrencyComparison(currencyPairId)
        val currencyComparisonDetails = fetchAndPersistRemoteData(currencyPairId, getDaysToFetchData())
        return currencyComparisonDetails!!
    }
}