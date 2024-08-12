package com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list

import com.alicasts.currencyinsight.common.Resource
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
    private val mapper: CurrencyPairMapper
) {
    operator fun invoke(): Flow<Resource<List<CurrencyPairListItemModel>>> = flow {
        try {
            emit(Resource.Loading())
            val currencyPairListDto = repository.getCurrencyPairList()
            val currencyPairList = mapper.fromDtoToModelList(currencyPairListDto)
            emit(Resource.Success(currencyPairList))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred."))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Couldn't reach server."))
        }
    }

}