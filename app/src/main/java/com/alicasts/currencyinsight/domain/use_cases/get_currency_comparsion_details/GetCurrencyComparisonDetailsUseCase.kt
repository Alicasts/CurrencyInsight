package com.alicasts.currencyinsight.domain.use_cases.get_currency_comparsion_details

import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails
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
    private val localRepository: LocalCurrencyPairRepository
) {
    operator fun invoke(currencyPairId: String, num: Int = 15): Flow<Resource<CurrencyComparisonDetails>> = flow {
        try {
            emit(Resource.Loading())
            val localData = localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId, num)

            val currencyComparisonDetails =
                if(shouldFetchRemoteData(localData))
                    fetchAndPersistRemoteData(currencyPairId, num)
                else localData

            emit(Resource.Success(currencyComparisonDetails!!))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred."))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Couldn't reach server."))
        }
    }

    private fun shouldFetchRemoteData(localData: CurrencyComparisonDetails?): Boolean {
        if (localData == null) return true
        val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        localData.let {
            val createDate: Date? = dateTimeFormatter.parse(it.createDate)

            createDate?.let { date ->
                val now = Date()
                val diffInMillis = now.time - date.time
                val diffInHours = diffInMillis / (1000 * 60 * 60)

                return diffInHours >= 4
            }
        }
        return false
    }

    private suspend fun fetchAndPersistRemoteData(currencyPairId: String, num: Int): CurrencyComparisonDetails? {
        val detailDto = remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num)
        localRepository.persistComparisonDetails(detailDto)
        return localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId, num)
    }
}