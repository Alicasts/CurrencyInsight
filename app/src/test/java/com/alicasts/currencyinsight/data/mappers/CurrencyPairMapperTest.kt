package com.alicasts.currencyinsight.data.mappers

import com.alicasts.currencyinsight.data.database.entities.CurrencyPairListEntity
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.mockData.CurrencyPairTestMockData
import com.alicasts.currencyinsight.data.mockData.CurrencyPairTestMockData.getJsonResponseAsString
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class CurrencyPairMapperTest {

    private lateinit var mapper: CurrencyPairMapper
    private lateinit var response: String

    @Before
    fun setup() {
        mapper = CurrencyPairMapper()
        response = getJsonResponseAsString()
    }

    @Test
    fun `fromDtoToModelList should correctly map DTO to Model`() {
        val dtoList = CurrencyPairTestMockData.parseCurrencyPairListResponse(response)
        val modelList = mapper.fromDtoToModelList(dtoList)

        modelList.forEachIndexed { index, model ->
            assertEquals(dtoList[index].currencyPairAbbreviations, model.currencyPairAbbreviations)
            assertEquals(dtoList[index].currencyPairFullNames, model.currencyPairFullNames)
        }
    }

    @Test
    fun `fromEntityToModelList should correctly map Entity to Model`() {
        val entityList = dtoListToEntityList(CurrencyPairTestMockData.parseCurrencyPairListResponse(
            getJsonResponseAsString()
        ))
        val modelList = mapper.fromEntityToModelList(entityList)

        modelList.forEachIndexed { index, model ->
            assertEquals(entityList[index].currencyPairAbbreviations, model.currencyPairAbbreviations)
            assertEquals(entityList[index].currencyPairFullNames, model.currencyPairFullNames)
        }
    }

    @Test
    fun `fromDtoToEntityList should correctly map DTO to Entity`() {
        val dtoList = CurrencyPairTestMockData.parseCurrencyPairListResponse(
            getJsonResponseAsString()
        )
        val entityList = mapper.fromDtoToEntityList(dtoList)

        entityList.forEachIndexed { index, entity ->
            assertEquals(dtoList[index].currencyPairAbbreviations, entity.currencyPairAbbreviations)
            assertEquals(dtoList[index].currencyPairFullNames, entity.currencyPairFullNames)
        }
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

    private fun dtoListToEntityList(dtoList: List<CurrencyPairListItemDto>): List<CurrencyPairListEntity> {
        return dtoList.map { dto ->
            CurrencyPairListEntity(
                currencyPairAbbreviations = dto.currencyPairAbbreviations,
                currencyPairFullNames = dto.currencyPairFullNames
            )
        }
    }
}