package com.alicasts.currencyinsight.data.mappers

import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyHistoricalData

class CurrencyComparisonMapper {

    fun mapToCurrencyComparisonDetails(response: CurrencyComparisonDetailDto): CurrencyComparisonDetails {

        val historicalData = mapToHistoricalDataList(response)

        return CurrencyComparisonDetails(
            code = response.code,
            codein = response.codein,
            name = response.name,
            high = response.high,
            low = response.low,
            varBid = response.varBid,
            pctChange = response.pctChange,
            bid = response.bid,
            ask = response.ask,
            timestamp = response.timestamp,
            createDate = response.createDate,
            historicalData = historicalData
        )
    }

    private fun mapToHistoricalDataList(response: CurrencyComparisonDetailDto): List<CurrencyHistoricalData> {
        val historicalData = response.historicalData.map { dto ->
            CurrencyHistoricalData(
                high = dto.high,
                low = dto.low,
                varBid = dto.varBid,
                pctChange = dto.pctChange,
                bid = dto.bid,
                ask = dto.ask,
                timestamp = dto.timestamp
            )
        }
        return historicalData
    }
}