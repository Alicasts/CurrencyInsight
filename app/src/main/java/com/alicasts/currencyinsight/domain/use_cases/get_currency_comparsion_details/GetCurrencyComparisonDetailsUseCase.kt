package com.alicasts.currencyinsight.domain.use_cases.get_currency_comparsion_details

import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.data.mappers.CurrencyComparisonMapper
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetCurrencyComparisonDetailsUseCase @Inject constructor(
    private val repository: CurrencyPairRepository,
    private val mapper: CurrencyComparisonMapper
) {
    operator fun invoke(currencyPairId: String, num: Int = 15): Flow<Resource<CurrencyComparisonDetails>> = flow {
        try {
            emit(Resource.Loading())
            val jsonResponse = repository.getCurrencyComparisonDetails(currencyPairId, num)
            val currencyComparisonDetails = mapper.mapToCurrencyComparisonDetails(jsonResponse)
            emit(Resource.Success(currencyComparisonDetails))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred."))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Couldn't reach server."))
        }
    }
}