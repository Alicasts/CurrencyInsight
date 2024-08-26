package com.alicasts.currencyinsight.domain.use_cases.get_currency_comparsion_details

import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.data.mockData.CurrencyComparisonWithDetailsTestMockData.returnMockCurrencyComparisonDetails
import com.alicasts.currencyinsight.data.mockData.CurrencyComparisonWithDetailsTestMockData.returnMockDto
import com.alicasts.currencyinsight.domain.repository.local.LocalCurrencyPairRepository
import com.alicasts.currencyinsight.domain.repository.remote.RemoteCurrencyPairRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
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
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        remoteRepository = mockk()
        localRepository = mockk()
        useCase = GetCurrencyComparisonDetailsUseCase(remoteRepository, localRepository)
    }

    @Test
    fun `should fetch and persist remote data when local data is outdated`() = runBlocking {
        val currencyPairId = "USD_BRL"
        val num = 15

        val remoteDto = returnMockDto()
        val outdatedLocalData = returnMockCurrencyComparisonDetails().copy(createDate = "2024-08-23 08:00:00")

        val upToDateLocalData = outdatedLocalData.copy(createDate = "2024-08-23 14:39:40")

        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId, num) } returns outdatedLocalData andThen upToDateLocalData
        coEvery { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) } returns remoteDto
        coEvery { localRepository.persistComparisonDetails(remoteDto) } just Runs

        val result = useCase.invoke(currencyPairId, num).toList()

        coVerify { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) }
        coVerify { localRepository.persistComparisonDetails(remoteDto) }

        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Success)
        assertEquals(upToDateLocalData, (result[1] as Resource.Success).data)
    }

    @Test
    fun `should return local data when it's up to date`() = runBlocking {
        val currencyPairId = "USD_BRL"
        val num = 15

        val upToDateLocalData = returnMockCurrencyComparisonDetails().copy(createDate = generateRecentDate())

        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId, num) } returns upToDateLocalData


        val result = useCase.invoke(currencyPairId, num).toList()

        coVerify(exactly = 0) { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) }
        coVerify(exactly = 0) { localRepository.persistComparisonDetails(any()) }

        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Success)
        assertEquals(upToDateLocalData, (result[1] as Resource.Success).data)
    }

    @Test
    fun `should fetch remote data when local data is null`() = runBlocking {
        val currencyPairId = "USD_BRL"
        val num = 15
        val remoteDataDto = returnMockDto()

        val localData = returnMockCurrencyComparisonDetails()

        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId, num) } returns null
        coEvery { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) } returns remoteDataDto
        coEvery { localRepository.persistComparisonDetails(any()) } just Runs
        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId, num) } returns localData

        val result = useCase.invoke(currencyPairId, num).toList()

        coVerify { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) }
        coVerify { localRepository.persistComparisonDetails(remoteDataDto) }

        assert(result[0] is Resource.Loading)
        assertEquals(localData, (result[1] as Resource.Success).data)
    }

    @Test
    fun `should emit error when HttpException is thrown`() = runBlocking {
        val currencyPairId = "USD_BRL"
        val num = 15

        val httpException = mockk<HttpException>()
        coEvery { httpException.localizedMessage } returns "An unexpected error occurred."
        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId, num) } returns null
        coEvery { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) } throws httpException

        val result = useCase.invoke(currencyPairId, num).toList()

        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Error)
        assertEquals("An unexpected error occurred.", (result[1] as Resource.Error).message)
    }

    @Test
    fun `should emit error when IOException is thrown`() = runBlocking {
        val currencyPairId = "USD_BRL"
        val num = 15

        coEvery { localRepository.getLocalCurrencyComparisonWithDetails(currencyPairId, num) } returns null
        coEvery { remoteRepository.getRemoteCurrencyComparisonWithDetails(currencyPairId, num) } throws IOException()

        val result = useCase.invoke(currencyPairId, num).toList()

        assert(result[0] is Resource.Loading)
        assert(result[1] is Resource.Error)
    }

    private fun generateRecentDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, -2)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}