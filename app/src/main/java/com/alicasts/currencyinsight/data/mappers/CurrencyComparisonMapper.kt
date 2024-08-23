package com.alicasts.currencyinsight.data.mappers

import com.alicasts.currencyinsight.data.database.comparison.CurrencyComparisonEntity
import com.alicasts.currencyinsight.data.database.comparison.CurrencyComparisonWithHistoricalData
import com.alicasts.currencyinsight.data.database.comparison.CurrencyHistoricalDataEntity
import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.data.dto.CurrencyHistoricalDataDto
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyHistoricalData
import com.google.gson.JsonArray

class CurrencyComparisonMapper {


    fun mapToEntity(dto: CurrencyComparisonDetailDto): CurrencyComparisonEntity {
        val comparisonCode = "${dto.code}-${dto.codein}"
        val createDate = dto.createDate

        return CurrencyComparisonEntity(
            comparisonCode = comparisonCode,
            code = dto.code,
            codein = dto.codein,
            name = dto.name,
            high = dto.high,
            low = dto.low,
            varBid = dto.varBid,
            pctChange = dto.pctChange,
            bid = dto.bid,
            ask = dto.ask,
            timestamp = dto.timestamp,
            createDate = createDate
        )
    }

    fun mapToHistoricalEntities(dtoList: List<CurrencyHistoricalDataDto>?, comparisonCode: String): List<CurrencyHistoricalDataEntity> {
        return dtoList?.map { dto ->
            CurrencyHistoricalDataEntity(
                comparisonCode = comparisonCode,
                high = dto.high,
                low = dto.low,
                varBid = dto.varBid,
                pctChange = dto.pctChange,
                bid = dto.bid,
                ask = dto.ask,
                timestamp = dto.timestamp
            )
        } ?: emptyList()
    }

    fun mapToCurrencyComparisonDetails(
        entityWithHistoricalData: CurrencyComparisonWithHistoricalData
    ): CurrencyComparisonDetails {
        val comparisonEntity = entityWithHistoricalData.comparison
        val historicalData = entityWithHistoricalData.historicalData.map { historicalEntity ->
            CurrencyHistoricalData(
                high = historicalEntity.high,
                low = historicalEntity.low,
                varBid = historicalEntity.varBid,
                pctChange = historicalEntity.pctChange,
                bid = historicalEntity.bid,
                ask = historicalEntity.ask,
                timestamp = historicalEntity.timestamp
            )
        }

        return CurrencyComparisonDetails(
            code = comparisonEntity.code,
            codein = comparisonEntity.codein,
            name = comparisonEntity.name,
            high = comparisonEntity.high,
            low = comparisonEntity.low,
            varBid = comparisonEntity.varBid,
            pctChange = comparisonEntity.pctChange,
            bid = comparisonEntity.bid,
            ask = comparisonEntity.ask,
            timestamp = comparisonEntity.timestamp,
            createDate = comparisonEntity.createDate,
            historicalData = historicalData
        )
    }

    fun parseCurrencyComparisonDetailsResponse(response: JsonArray): CurrencyComparisonDetailDto {
        val historicalData = response.drop(1).map { element ->
            val jsonObject = element.asJsonObject
            CurrencyHistoricalDataDto(
                high = jsonObject["high"].asString,
                low = jsonObject["low"].asString,
                varBid = jsonObject["varBid"].asString,
                pctChange = jsonObject["pctChange"].asString,
                bid = jsonObject["bid"].asString,
                ask = jsonObject["ask"].asString,
                timestamp = jsonObject["timestamp"].asString
            )
        }

        val jsonObject = response[0].asJsonObject

        return CurrencyComparisonDetailDto(
            code = jsonObject["code"]?.asString ?: "",
            codein = jsonObject["codein"]?.asString ?: "",
            name = jsonObject["name"]?.asString ?: "",
            high = jsonObject["high"].asString,
            low = jsonObject["low"].asString,
            varBid = jsonObject["varBid"].asString,
            pctChange = jsonObject["pctChange"].asString,
            bid = jsonObject["bid"].asString,
            ask = jsonObject["ask"].asString,
            timestamp = jsonObject["timestamp"].asString,
            createDate = jsonObject["create_date"]?.asString ?: "",
            historicalData = historicalData
        )
    }
}
