package com.alicasts.currencyinsight.data.mappers

import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class CurrencyPairMapperTest {

    private val mapper = CurrencyPairMapper()


    @Test
    fun `test valid DTO to model mapping`() {
        val dto = CurrencyPairListItemDto("USD/EUR", "US Dollar/Euro")
        val model = mapper.fromDtoToModelList(listOf(dto)).first()

        assertEquals("USD/EUR", model.currencyPairAbbreviations)
        assertEquals("US Dollar/Euro", model.currencyPairFullNames)
    }

    @Test
    fun `test mapping with multiple valid items`() {
        val dtoList = listOf(
            CurrencyPairListItemDto("USD/EUR", "US Dollar/Euro"),
            CurrencyPairListItemDto("GBP/USD", "British Pound/US Dollar"),
            CurrencyPairListItemDto("JPY/CHF", "Japanese Yen/Swiss Franc")
        )
        val modelList = mapper.fromDtoToModelList(dtoList)

        assertEquals(3, modelList.size)
        assertEquals("USD/EUR", modelList[0].currencyPairAbbreviations)
        assertEquals("US Dollar/Euro", modelList[0].currencyPairFullNames)

        assertEquals("GBP/USD", modelList[1].currencyPairAbbreviations)
        assertEquals("British Pound/US Dollar", modelList[1].currencyPairFullNames)

        assertEquals("JPY/CHF", modelList[2].currencyPairAbbreviations)
        assertEquals("Japanese Yen/Swiss Franc", modelList[2].currencyPairFullNames)
    }

    @Test
    fun `test empty list`() {
        val result = mapper.fromDtoToModelList(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `test mapping with special characters`() {
        val dto = CurrencyPairListItemDto("CNY/JPY", "Chinese Yuan/日本円")
        val model = mapper.fromDtoToModelList(listOf(dto)).first()

        assertEquals("CNY/JPY", model.currencyPairAbbreviations)
        assertEquals("Chinese Yuan/日本円", model.currencyPairFullNames)
    }

    @Test
    fun `test mapping with different string cases`() {
        val dto = CurrencyPairListItemDto("usd/eur", "us dollar/euro")
        val model = mapper.fromDtoToModelList(listOf(dto)).first()

        assertEquals("usd/eur", model.currencyPairAbbreviations)
        assertEquals("us dollar/euro", model.currencyPairFullNames)
    }
}