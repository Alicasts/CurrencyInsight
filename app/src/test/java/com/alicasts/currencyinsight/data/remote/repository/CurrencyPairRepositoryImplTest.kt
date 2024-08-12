package com.alicasts.currencyinsight.data.remote.repository

import com.alicasts.currencyinsight.common.parseCurrencyPairListResponse
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import com.google.gson.JsonObject
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CurrencyPairRepositoryImplTest {

    private lateinit var repository: CurrencyPairRepository
    private val api: CoinAwesomeApi = mockk()

    @Before
    fun setUp() {
        repository = CurrencyPairRepositoryImpl(api)
    }

    @Test
    fun `test getCurrencyPairList returns expected data`() = runBlocking {
        val expectedList = listOf(
            CurrencyPairListItemDto("USD/EUR", "US Dollar/Euro"),
            CurrencyPairListItemDto("GBP/USD", "British Pound/US Dollar")
        )

        coEvery { api.getCurrencyPairList() } returns mockk()
        mockkStatic("com.alicasts.currencyinsight.common.UtilsKt")
        coEvery { parseCurrencyPairListResponse(any()) } returns expectedList

        val result = repository.getCurrencyPairList()

        assertEquals(expectedList, result)
    }

    @Test(expected = Exception::class)
    fun `test getCurrencyPairList throws exception`() = runBlocking {
        coEvery { api.getCurrencyPairList() } throws Exception("API Error")

        repository.getCurrencyPairList()

        coVerify(exactly = 1) { api.getCurrencyPairList() }
    }

}