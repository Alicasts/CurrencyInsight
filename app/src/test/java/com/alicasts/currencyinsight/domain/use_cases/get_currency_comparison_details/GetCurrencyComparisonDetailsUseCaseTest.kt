package com.alicasts.currencyinsight.domain.use_cases.get_currency_comparison_details

import android.content.SharedPreferences
import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.data.mockData.CurrencyComparisonWithDetailsTestMockData.returnMockCurrencyComparisonDetails
import com.alicasts.currencyinsight.data.mockData.CurrencyComparisonWithDetailsTestMockData.returnMockDto
import com.alicasts.currencyinsight.domain.repository.local.LocalCurrencyPairRepository
import com.alicasts.currencyinsight.domain.repository.remote.RemoteCurrencyPairRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GetCurrencyComparisonDetailsUseCaseTest {

    private lateinit var useCase: GetCurrencyComparisonDetailsUseCase
    private lateinit var remoteRepository: RemoteCurrencyPairRepository
    private lateinit var localRepository: LocalCurrencyPairRepository
    private lateinit var sharedPreferences: SharedPreferences
    private val currencyPairId = "USD_BRL"
    private val num = 15

    @Before
    fun setUp() {
        remoteRepository = mockk()
        localRepository = mockk()
        sharedPreferences = mockk()
        useCase = GetCurrencyComparisonDetailsUseCase(remoteRepository, localRepository, sharedPreferences)

        // Configuração comum para todos os testes
        every { sharedPreferences.getInt("days_to_fetch_data", 15) } returns 15
        coEvery { localRepository.deleteCurrencyComparison(currencyPairId) } just Runs
        coEvery { localRepository.clearSelectedHistoricalData(currencyPairId) } just Runs
    }

    @Test
    fun `should fetch and persist remote data when local data is outdated`() = runBlocking {
        val remoteDto = returnMockDto()
        val outdatedLocalData = returnMockCurrencyComparisonDetails().copy(createDate = "2024-08-23 08:00:00")
        val upToDateLocalData = outdatedLocalData.copy(createDate = "2024-08-23 14:39:40")

        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId) } returns outdatedLocalData andThen upToDateLocalData
        coEvery { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) } returns remoteDto
        coEvery { localRepository.persistComparisonDetails(remoteDto) } just Runs

        val result = useCase.invoke(currencyPairId).toList()

        coVerify { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) }
        coVerify { localRepository.persistComparisonDetails(remoteDto) }

        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Success)
        assertEquals(upToDateLocalData, (result[1] as Resource.Success).data)
    }

    @Test
    fun `should fetch remote data when local data is null`() = runBlocking {
        val remoteDataDto = returnMockDto()
        val localData = returnMockCurrencyComparisonDetails()

        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId) } returns null
        coEvery { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) } returns remoteDataDto
        coEvery { localRepository.persistComparisonDetails(remoteDataDto) } just Runs
        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId) } returns localData

        val result = useCase.invoke(currencyPairId).toList()

        coVerify { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) }
        coVerify { localRepository.persistComparisonDetails(remoteDataDto) }

        assert(result[0] is Resource.Loading)
        assertEquals(localData, (result[1] as Resource.Success).data)
    }

    @Test
    fun `should emit error when HttpException is thrown`() = runBlocking {
        val httpException = mockk<HttpException>()
        coEvery { httpException.localizedMessage } returns "An unexpected error occurred."
        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId) } returns null
        coEvery { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) } throws httpException

        val result = useCase.invoke(currencyPairId).toList()

        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Error)
        assertEquals("An unexpected error occurred.", (result[1] as Resource.Error).message)
    }

    @Test
    fun `should emit error when IOException is thrown`() = runBlocking {
        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId) } returns null
        coEvery { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) } throws IOException()

        val result = useCase.invoke(currencyPairId).toList()

        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Error)
    }
}