package com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list

import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel
import com.alicasts.currencyinsight.domain.repository.local.LocalCurrencyPairRepository
import com.alicasts.currencyinsight.domain.repository.remote.RemoteCurrencyPairRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetCurrencyPairListUseCase @Inject constructor(
    private val remoteRepository: RemoteCurrencyPairRepository,
    private val localRepository: LocalCurrencyPairRepository
) {

    operator fun invoke(): Flow<Resource<List<CurrencyPairListItemModel>>> = flow {
        try {
            emit(Resource.Loading())

            val currencyPairList =
                if(shouldFetchNewData()) {
                    val currencyPairListDto = remoteRepository.getCurrencyPairsList()
                    localRepository.persistUpdatedList(currencyPairListDto)
                    remoteRepository.convertDtoToModel(currencyPairListDto)
                }
                else localRepository.getCurrencyPairsModelList()

            emit(Resource.Success(currencyPairList))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred."))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Couldn't reach server."))
        }
    }

    private suspend fun shouldFetchNewData(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastFetchDate = localRepository.getLastFetchDate()

        val oneDayInMillis = 24 * 60 * 60 * 1000
        val isIntervalGreaterThanOneDay = currentTime - lastFetchDate > oneDayInMillis
        return isIntervalGreaterThanOneDay
    }
}