package com.alicasts.currencyinsight.domain.use_cases.get_currency_comparsion_details

import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.data.database.comparison.CurrencyComparisonWithHistoricalData
import com.alicasts.currencyinsight.data.dto.CurrencyHistoricalDataDto
import com.alicasts.currencyinsight.data.mappers.CurrencyComparisonMapper
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetCurrencyComparisonDetailsUseCase @Inject constructor(
    private val repository: CurrencyPairRepository,
    private val mapper: CurrencyComparisonMapper
) {
    operator fun invoke(currencyPairId: String, num: Int = 15): Flow<Resource<CurrencyComparisonDetails>> = flow {
        try {
            emit(Resource.Loading())
            val localData = repository.getLocalCurrencyComparisonWithDetails(currencyPairId, num)

            val currencyComparisonDetails = when {
                shouldFetchRemoteData(localData) -> {
                    fetchAndPersistRemoteData(currencyPairId, num)
                }
                else -> localData
            }

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

    private suspend fun fetchAndPersistRemoteData(currencyPairId: String, num: Int): CurrencyComparisonDetails {
        val detailDto = repository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num)
        val comparisonEntity = mapper.mapToEntity(detailDto)
        val historicalDataDtos: List<CurrencyHistoricalDataDto> = detailDto.historicalData
        val historicalDataEntities = mapper.mapToHistoricalEntities(historicalDataDtos, comparisonEntity.comparisonCode)

        repository.insertCurrencyComparison(comparisonEntity)
        repository.insertHistoricalData(historicalDataEntities)

        val currencyComparisonWithHistoricalData = CurrencyComparisonWithHistoricalData(
            comparison = comparisonEntity,
            historicalData = historicalDataEntities
        )

        return mapper.mapToCurrencyComparisonDetails(currencyComparisonWithHistoricalData)
    }
}