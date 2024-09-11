package com.alicasts.currencyinsight.data.repository.remote

import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.mappers.CurrencyComparisonMapper
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.mockData.CurrencyComparisonWithDetailsTestMockData
import com.alicasts.currencyinsight.data.mockData.CurrencyComparisonWithDetailsTestMockData.parseCurrencyComparisonDetailsResponse
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel
import com.google.gson.JsonObject
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class RemoteCurrencyPairRepositoryImplTest {

    private lateinit var api: CoinAwesomeApi
    private lateinit var currencyPairMapper: CurrencyPairMapper
    private lateinit var currencyComparisonMapper: CurrencyComparisonMapper
    private lateinit var repository: RemoteCurrencyPairRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        currencyPairMapper = mockk()
        currencyComparisonMapper = mockk()

        repository = RemoteCurrencyPairRepositoryImpl(
            api = api,
            currencyPairMapper = currencyPairMapper,
            currencyComparisonMapper = currencyComparisonMapper)
    }

    @Test
    fun `getCurrencyPairsList should fetch data from API and map it using currencyPairMapper`() = runBlocking {
        val response = mockk<JsonObject>()
        val parsedResponse = listOf(
            CurrencyPairListItemDto("USD/EUR", "US Dollar/Euro"),
            CurrencyPairListItemDto("GBP/USD", "British Pound/US Dollar")
        )

        coEvery { api.getCurrencyPairList() } returns response
        every { currencyPairMapper.parseCurrencyPairListResponse(response) } returns parsedResponse

        val result = repository.getCurrencyPairsList()

        coVerify { api.getCurrencyPairList() }
        coVerify { currencyPairMapper.parseCurrencyPairListResponse(response) }
        assertEquals(parsedResponse, result)
    }

    @Test
    fun `getRemoteCurrencyComparisonWithDetails should fetch data from API and map it using currencyComparisonMapper`() = runBlocking {
        val currencyPairId = "USD_BRL"
        val days = 15

        val response = CurrencyComparisonWithDetailsTestMockData.getCurrencyComparisonJsonArray()

        val parsedResponse = parseCurrencyComparisonDetailsResponse(response)

        coEvery { api.getCurrencyComparisonWithDetails(currencyPairId, days) } returns response
        coEvery { currencyComparisonMapper.parseCurrencyComparisonDetailsResponse(response) } returns parsedResponse

        val result = repository.getRemoteCurrencyComparisonWithDetails(currencyPairId, days)

        coVerify { api.getCurrencyComparisonWithDetails(currencyPairId, days) }
        coVerify { currencyComparisonMapper.parseCurrencyComparisonDetailsResponse(response) }
        assertEquals(parsedResponse, result)
    }


    @Test
    fun `convertDtoToModel should map currencyPairListDto to models using currencyPairMapper`() = runBlocking {
        val dtoList = listOf(
            CurrencyPairListItemDto("USD/EUR", "US Dollar/Euro"),
            CurrencyPairListItemDto("GBP/USD", "British Pound/US Dollar")
        )
        val modelList = listOf(
            CurrencyPairListItemModel("USD/EUR", "US Dollar/Euro"),
            CurrencyPairListItemModel("GBP/USD", "British Pound/US Dollar")
        )

        every { currencyPairMapper.fromDtoToModelList(dtoList) } returns modelList

        val result = repository.convertDtoToModel(dtoList)

        coVerify { currencyPairMapper.fromDtoToModelList(dtoList) }
        assertEquals(modelList, result)
    }
}