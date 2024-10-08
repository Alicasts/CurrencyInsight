package com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list

import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.data.database.entities.CurrencyPairListEntity
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.mockData.CurrencyPairTestMockData
import com.alicasts.currencyinsight.domain.repository.local.LocalCurrencyPairRepository
import com.alicasts.currencyinsight.domain.repository.remote.RemoteCurrencyPairRepository
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
    private lateinit var remoteRepository: RemoteCurrencyPairRepository
    private lateinit var localRepository: LocalCurrencyPairRepository
    private lateinit var mapper: CurrencyPairMapper

    @Before
    fun setup() {
        remoteRepository = mockk()
        localRepository = mockk()
        mapper = CurrencyPairMapper()
        useCase = GetCurrencyPairListUseCase(remoteRepository, localRepository)
    }

    @Test
    fun `should fetch new data when necessary and return Success`() = runBlocking {
        val dtoList = CurrencyPairTestMockData.parseCurrencyPairListResponse(CurrencyPairTestMockData.getJsonResponseAsString())
        val modelList = mapper.fromDtoToModelList(dtoList)

        coEvery { localRepository.getLastFetchDate() } returns System.currentTimeMillis() - (25 * 60 * 60 * 1000)
        coEvery { remoteRepository.getCurrencyPairsList() } returns dtoList
        coEvery { localRepository.persistUpdatedList(dtoList) } returns Unit
        coEvery { remoteRepository.convertDtoToModel(dtoList) } returns modelList

        val emissions = useCase.invoke().toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(modelList, (emissions[1] as Resource.Success).data)

        coVerify { remoteRepository.getCurrencyPairsList() }
        coVerify { localRepository.persistUpdatedList(dtoList) }
        coVerify { remoteRepository.convertDtoToModel(dtoList) }
    }

    @Test
    fun `should return data from database and skip API call when last fetch is recent`() = runBlocking {
        val entityList = CurrencyPairTestMockData.parseCurrencyPairListResponse(CurrencyPairTestMockData.getJsonResponseAsString()).map { dto ->
            CurrencyPairListEntity(
                currencyPairAbbreviations = dto.currencyPairAbbreviations,
                currencyPairFullNames = dto.currencyPairFullNames
            )
        }
        val modelList = mapper.fromEntityToModelList(entityList)

        coEvery { localRepository.getLastFetchDate() } returns System.currentTimeMillis()
        coEvery { localRepository.getCurrencyPairsModelList() } returns modelList

        val emissions = useCase.invoke().toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(modelList, (emissions[1] as Resource.Success).data)

        coVerify(exactly = 0) { remoteRepository.getCurrencyPairsList() }
        coVerify(exactly = 0) { localRepository.persistUpdatedList(any()) }
    }

    @Test
    fun `should return data from database when last fetch is recent`() = runBlocking {
        val entityList = CurrencyPairTestMockData.parseCurrencyPairListResponse(CurrencyPairTestMockData.getJsonResponseAsString()).map { dto ->
            CurrencyPairListEntity(
                currencyPairAbbreviations = dto.currencyPairAbbreviations,
                currencyPairFullNames = dto.currencyPairFullNames
            )
        }
        val modelList = mapper.fromEntityToModelList(entityList)

        coEvery { localRepository.getLastFetchDate() } returns System.currentTimeMillis()
        coEvery { localRepository.getCurrencyPairsModelList() } returns modelList

        val emissions = useCase.invoke().toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(modelList, (emissions[1] as Resource.Success).data)
    }

    @Test
    fun `should not fetch new data from API when last fetch is recent`() = runBlocking {
        coEvery { localRepository.getLastFetchDate() } returns System.currentTimeMillis()
        coEvery { localRepository.getCurrencyPairsModelList() } returns emptyList()

        useCase.invoke().toList()

        coVerify(exactly = 0) { remoteRepository.getCurrencyPairsList() }
        coVerify(exactly = 0) { localRepository.persistUpdatedList(any()) }
    }

    @Test
    fun `should emit Error when HttpException is thrown`() = runBlocking {
        val errorResponse = Response.error<ResponseBody>(
            500,
            "Internal Server Error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        coEvery { localRepository.getLastFetchDate() } returns System.currentTimeMillis() - (25 * 60 * 60 * 1000)
        coEvery { remoteRepository.getCurrencyPairsList() } throws HttpException(errorResponse)

        val emissions = useCase.invoke().toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals("HTTP 500 Response.error()", (emissions[1] as Resource.Error).message)
    }

    @Test
    fun `should emit Error when IOException is thrown`() = runBlocking {
        coEvery { localRepository.getLastFetchDate() } returns System.currentTimeMillis() - (25 * 60 * 60 * 1000)
        coEvery { remoteRepository.getCurrencyPairsList() } throws IOException()

        val emissions = useCase.invoke().toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals("Couldn't reach server.", (emissions[1] as Resource.Error).message)
    }
}