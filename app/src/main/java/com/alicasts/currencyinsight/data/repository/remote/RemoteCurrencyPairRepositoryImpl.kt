package com.alicasts.currencyinsight.data.repository.remote

import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.mappers.CurrencyComparisonMapper
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel
import com.alicasts.currencyinsight.domain.repository.remote.RemoteCurrencyPairRepository
import javax.inject.Inject

class RemoteCurrencyPairRepositoryImpl @Inject constructor(
    private val api: CoinAwesomeApi,
    private val currencyPairMapper: CurrencyPairMapper,
    private val currencyComparisonMapper: CurrencyComparisonMapper
) : RemoteCurrencyPairRepository {

    override suspend fun getCurrencyPairsList(): List<CurrencyPairListItemDto> {
        val response = api.getCurrencyPairList()
        return currencyPairMapper.parseCurrencyPairListResponse(response)
    }

    override suspend fun getRemoteCurrencyComparisonWithDetails(currencyPairId: String, days: Int): CurrencyComparisonDetailDto {
        val response = api.getCurrencyComparisonWithDetails(currencyPairId, days)
        return currencyComparisonMapper.parseCurrencyComparisonDetailsResponse(response)
    }

    override suspend fun convertDtoToModel(currencyPairListDto: List<CurrencyPairListItemDto>):
            List<CurrencyPairListItemModel> {
        return currencyPairMapper.fromDtoToModelList(currencyPairListDto)
    }
}