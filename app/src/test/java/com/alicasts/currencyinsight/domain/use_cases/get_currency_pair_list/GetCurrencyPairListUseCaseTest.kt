package com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list

import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.data.database.list.CurrencyPairListEntity
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.mockData.CurrencyPairTestMockData
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class GetCurrencyPairListUseCaseTest {

    private lateinit var useCase: GetCurrencyPairListUseCase
    private lateinit var repository: CurrencyPairRepository
    private lateinit var mapper: CurrencyPairMapper

    @Before
    fun setup() {
        repository = mockk()
        mapper = CurrencyPairMapper()
        useCase = GetCurrencyPairListUseCase(repository, mapper)
    }
    @Test
    fun `should fetch new data when necessary and return Success`() = runBlocking {
        val dtoList = CurrencyPairTestMockData.parseCurrencyPairListResponse(CurrencyPairTestMockData.getJsonResponseAsString())
        val modelList = mapper.fromDtoToModelList(dtoList)

        coEvery { repository.getLastFetchDate() } returns System.currentTimeMillis() - (25 * 60 * 60 * 1000)
        coEvery { repository.getRemoteCurrencyPairList() } returns dtoList
        coEvery { repository.saveCurrencyPairsToDatabase(dtoList) } returns Unit
        coEvery { repository.updateLastFetchDate() } returns Unit
        val emissions = useCase.invoke().toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(modelList, (emissions[1] as Resource.Success).data)
        coVerify { repository.getRemoteCurrencyPairList() }
        coVerify { repository.saveCurrencyPairsToDatabase(dtoList) }
        coVerify { repository.updateLastFetchDate() }
    }

    @Test
    fun `should return data from database and skip API call when last fetch is recent`() = runBlocking {
        val entityList = CurrencyPairTestMockData.parseCurrencyPairListResponse(CurrencyPairTestMockData.getJsonResponseAsString()).map { dto ->
            CurrencyPairListEntity(
                id = dto.currencyPairAbbreviations,
                currencyPairAbbreviations = dto.currencyPairAbbreviations,
                currencyPairFullNames = dto.currencyPairFullNames
            )
        }
        val modelList = mapper.fromEntityToModelList(entityList)

        coEvery { repository.getLastFetchDate() } returns System.currentTimeMillis()
        coEvery { repository.getLocalCurrencyPairsList() } returns entityList

        val emissions = useCase.invoke().toList()

        assertTrue(emissions[0] is Resource.Loading)

        assertTrue(emissions[1] is Resource.Success)
        assertEquals(modelList, (emissions[1] as Resource.Success).data)

        coVerify(exactly = 0) { repository.getRemoteCurrencyPairList() }
        coVerify(exactly = 0) { repository.saveCurrencyPairsToDatabase(any()) }
    }

    @Test
    fun `should return data from database when last fetch is recent`() = runBlocking {
        val entityList = CurrencyPairTestMockData.parseCurrencyPairListResponse(CurrencyPairTestMockData.getJsonResponseAsString()).map { dto ->
            CurrencyPairListEntity(
                id = dto.currencyPairAbbreviations,
                currencyPairAbbreviations = dto.currencyPairAbbreviations,
                currencyPairFullNames = dto.currencyPairFullNames
            )
        }
        val modelList = mapper.fromEntityToModelList(entityList)
        coEvery { repository.getLastFetchDate() } returns System.currentTimeMillis()
        coEvery { repository.getLocalCurrencyPairsList() } returns entityList
        val emissions = useCase.invoke().toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(modelList, (emissions[1] as Resource.Success).data)
    }

    @Test
    fun `should not fetch new data from API when last fetch is recent`() = runBlocking {
        coEvery { repository.getLastFetchDate() } returns System.currentTimeMillis()
        coEvery { repository.getLocalCurrencyPairsList() } returns emptyList()

        useCase.invoke().toList()

        coVerify(exactly = 0) { repository.getRemoteCurrencyPairList() }
        coVerify(exactly = 0) { repository.saveCurrencyPairsToDatabase(any()) }
    }

    @Test
    fun `should emit Error when HttpException is thrown`() = runBlocking {
        val errorResponse = Response.error<ResponseBody>(
            500,
            "Internal Server Error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        coEvery { repository.getLastFetchDate() } returns System.currentTimeMillis() - (25 * 60 * 60 * 1000)
        coEvery { repository.getRemoteCurrencyPairList() } throws HttpException(errorResponse)

        val emissions = useCase.invoke().toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals("HTTP 500 Response.error()", (emissions[1] as Resource.Error).message)
    }

    @Test
    fun `should emit Error when IOException is thrown`() = runBlocking {
        coEvery { repository.getLastFetchDate() } returns System.currentTimeMillis() - (25 * 60 * 60 * 1000)
        coEvery { repository.getRemoteCurrencyPairList() } throws IOException()

        val emissions = useCase.invoke().toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals("Couldn't reach server.", (emissions[1] as Resource.Error).message)
    }
}