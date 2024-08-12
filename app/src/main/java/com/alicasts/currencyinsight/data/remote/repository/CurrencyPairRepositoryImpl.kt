package com.alicasts.currencyinsight.data.remote.repository

import com.alicasts.currencyinsight.common.parseCurrencyPairListResponse
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import javax.inject.Inject

class CurrencyPairRepositoryImpl @Inject constructor(
    private val api: CoinAwesomeApi
) : CurrencyPairRepository {
    override suspend fun getCurrencyPairList(): List<CurrencyPairListItemDto> {
        val response = api.getCurrencyPairList()
        return parseCurrencyPairListResponse(response)
    }

}