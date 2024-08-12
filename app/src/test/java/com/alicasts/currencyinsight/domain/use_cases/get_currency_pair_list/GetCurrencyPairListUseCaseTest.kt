package com.alicasts.currencyinsight.domain.use_cases.get_currency_pair_list

import com.alicasts.currencyinsight.common.Resource
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel
import com.alicasts.currencyinsight.domain.repository.CurrencyPairRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class GetCurrencyPairListUseCaseTest {

    private val repository = mockk<CurrencyPairRepository>()
    private val mapper = mockk<CurrencyPairMapper>()
    private val useCase = GetCurrencyPairListUseCase(repository, mapper)

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test successful data fetching`() = runTest {
        val dtoList = listOf(CurrencyPairListItemDto("USD/EUR", "US Dollar/Euro"))
        val modelList = listOf(CurrencyPairListItemModel("USD/EUR", "US Dollar/Euro"))

        coEvery { repository.getCurrencyPairList() } returns dtoList
        every { mapper.fromDtoToModelList(dtoList) } returns modelList

        val result = useCase().toList()

        assertEquals(2, result.size)
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Success && result[1].data == modelList)
    }

    @Test
    fun `test HTTP error handling`() = runTest {
        coEvery { repository.getCurrencyPairList() } throws HttpException(Response.error<Any>(404, "".toResponseBody()))

        val result = useCase().toList()
        println(result[1].message)

        assertEquals(2, result.size)
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Error)
    }
}