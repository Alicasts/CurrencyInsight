package com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list

import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetCurrencyPairListUseCase @Inject constructor(
    private val repository: CurrencyPairRepository,
    private val mapper: CurrencyPairMapper
) {

    operator fun invoke(): Flow<Resource<List<CurrencyPairListItemModel>>> = flow {
        try {
            emit(Resource.Loading())
            val currencyPairList: List<CurrencyPairListItemModel>

            if (shouldFetchNewData()) {
                val currencyPairListDto = repository.getCurrencyPairList()
                persistUpdatedList(currencyPairListDto)
                currencyPairList = mapper.fromDtoToModelList(currencyPairListDto)
            } else {
                currencyPairList = mapper.fromEntityToModelList(
                    repository.getCurrencyPairsFromDatabase()
                )
            }
            emit(Resource.Success(currencyPairList))
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

    private suspend fun shouldFetchNewData(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastFetchDate = repository.getLastFetchDate()

        val oneDayInMillis = 24 * 60 * 60 * 1000
        val isIntervalGreaterThanOneDay = currentTime - lastFetchDate > oneDayInMillis
        return isIntervalGreaterThanOneDay
    }
}