package com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list

import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetCurrencyPairListUseCase @Inject constructor(
    private val repository: CurrencyPairRepository,
) {
    private val mapper = CurrencyPairMapper()
    private lateinit var currencyPairList: List<CurrencyPairListItemModel>

    operator fun invoke(): Flow<Resource<List<CurrencyPairListItemModel>>> = flow {
        try {
            emit(Resource.Loading())

            if (shouldFetchNewData()) {
                val currencyPairListDto = returnCurrencyListDto()
                persistUpdatedList(currencyPairListDto)

                currencyPairList  = mapper.fromDtoToModelList(currencyPairListDto)
                emit(Resource.Success(currencyPairList))
            } else {
                currencyPairList =
                    mapper.fromEntityToModelList(
                    repository.getCurrencyPairsFromDatabase()
                )
                emit(Resource.Success(currencyPairList))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred."))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Couldn't reach server."))
        }
    }

    private suspend fun persistUpdatedList(currencyPairListDto: List<CurrencyPairListItemDto>) {
        repository.updateLastFetchDate()
        repository.saveCurrencyPairsToDatabase(currencyPairListDto)
    }

    private suspend fun returnCurrencyListDto(): List<CurrencyPairListItemDto> {
        val currencyPairListDto = repository.getCurrencyPairList()
        return currencyPairListDto
    }

    private suspend fun shouldFetchNewData(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastFetchDate = repository.getLastFetchDate()

        val oneDayInMillis = 24 * 60 * 60 * 1000
        val isIntervalGreaterThanOneDay = currentTime - lastFetchDate > oneDayInMillis
        return isIntervalGreaterThanOneDay
    }
}