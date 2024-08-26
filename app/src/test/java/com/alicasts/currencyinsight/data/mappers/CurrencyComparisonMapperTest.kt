package com.alicasts.currencyinsight.data.mappers

import com.alicasts.currencyinsight.data.database.relations.CurrencyComparisonWithHistoricalData
import com.alicasts.currencyinsight.data.mockData.CurrencyComparisonWithDetailsTestMockData
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CurrencyComparisonMapperTest {

    private lateinit var mapper: CurrencyComparisonMapper

    @Before
    fun setUp() {
        mapper = CurrencyComparisonMapper()
    }

    @Test
    fun `mapToEntity should correctly map CurrencyComparisonDetailDto to CurrencyComparisonEntity`() {
        val dto = CurrencyComparisonWithDetailsTestMockData.parseCurrencyComparisonDetailsResponse(
            CurrencyComparisonWithDetailsTestMockData.getCurrencyComparisonJsonArray()
        )

        val entity = mapper.mapToEntity(dto)

        assertEquals("USD-BRL", entity.comparisonCode)
        assertEquals("USD", entity.code)
        assertEquals("BRL", entity.codein)
        assertEquals("Dólar Americano/Real Brasileiro", entity.name)
        assertEquals("5.6061", entity.high)
        assertEquals("5.4716", entity.low)
        assertEquals("-0.0785", entity.varBid)
        assertEquals("-1.41", entity.pctChange)
        assertEquals("5.5037", entity.bid)
        assertEquals("5.506", entity.ask)
        assertEquals("1724434780", entity.timestamp)
        assertEquals("2024-08-23 14:39:40", entity.createDate)
    }

    @Test
    fun `mapToHistoricalEntities should correctly map list of CurrencyHistoricalDataDto to list of CurrencyHistoricalDataEntity`() {
        val dto = CurrencyComparisonWithDetailsTestMockData.parseCurrencyComparisonDetailsResponse(
            CurrencyComparisonWithDetailsTestMockData.getCurrencyComparisonJsonArray()
        )

        val entities = mapper.mapToHistoricalEntities(dto.historicalData, "USD-BRL")

        assertEquals(14, entities.size)
        assertEquals("USD-BRL", entities[0].comparisonCode)
        assertEquals("5.5936", entities[0].high)
        assertEquals("5.5844", entities[0].low)
        assertEquals("0.0082", entities[0].varBid)
        assertEquals("0.15", entities[0].pctChange)
        assertEquals("5.5911", entities[0].bid)
        assertEquals("5.5921", entities[0].ask)
        assertEquals("1724361875", entities[0].timestamp)
    }

    @Test
    fun `mapToCurrencyComparisonDetails should correctly map CurrencyComparisonWithHistoricalData to CurrencyComparisonDetails`() {
        val dto = CurrencyComparisonWithDetailsTestMockData.parseCurrencyComparisonDetailsResponse(
            CurrencyComparisonWithDetailsTestMockData.getCurrencyComparisonJsonArray()
        )

        val entity = mapper.mapToEntity(dto)
        val historicalEntities = mapper.mapToHistoricalEntities(dto.historicalData, entity.comparisonCode)

        val entityWithHistoricalData = CurrencyComparisonWithHistoricalData(
            comparison = entity,
            historicalData = historicalEntities
        )

        val details = mapper.mapToCurrencyComparisonDetails(entityWithHistoricalData)

        assertEquals("USD", details.code)
        assertEquals("BRL", details.codein)
        assertEquals("Dólar Americano/Real Brasileiro", details.name)
        assertEquals("5.6061", details.high)
        assertEquals("5.4716", details.low)
        assertEquals("-0.0785", details.varBid)
        assertEquals("-1.41", details.pctChange)
        assertEquals("5.5037", details.bid)
        assertEquals("5.506", details.ask)
        assertEquals("1724434780", details.timestamp)
        assertEquals("2024-08-23 14:39:40", details.createDate)
        assertEquals(14, details.historicalData.size)
    }

    @Test
    fun `parseCurrencyComparisonDetailsResponse should correctly parse JsonArray to CurrencyComparisonDetailDto`() {
        val response = CurrencyComparisonWithDetailsTestMockData.getCurrencyComparisonJsonArray()

        val dto = mapper.parseCurrencyComparisonDetailsResponse(response)

        assertEquals("USD", dto.code)
        assertEquals("BRL", dto.codein)
        assertEquals("Dólar Americano/Real Brasileiro", dto.name)
        assertEquals("5.6061", dto.high)
        assertEquals("5.4716", dto.low)
        assertEquals("-0.0785", dto.varBid)
        assertEquals("-1.41", dto.pctChange)
        assertEquals("5.5037", dto.bid)
        assertEquals("5.506", dto.ask)
        assertEquals("1724434780", dto.timestamp)
        assertEquals("2024-08-23 14:39:40", dto.createDate)
        assertEquals(14, dto.historicalData.size)
    }
}