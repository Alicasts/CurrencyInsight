package com.alicasts.currencyinsight.data.remote.repository

import android.content.SharedPreferences
import com.alicasts.currencyinsight.data.database.CurrencyPairDao
import com.alicasts.currencyinsight.data.database.CurrencyPairEntity
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.mockData.CurrencyPairTestMockData
import com.alicasts.currencyinsight.data.mockData.CurrencyPairTestMockData.getJsonResponse
import com.alicasts.currencyinsight.data.remote.CoinAwesomeApi
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class CurrencyPairRepositoryImplTest {

    private lateinit var repository: CurrencyPairRepositoryImpl
    private lateinit var api: CoinAwesomeApi
    private lateinit var dao: CurrencyPairDao
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mapper: CurrencyPairMapper

    @Before
    fun setup() {
        api = mock(CoinAwesomeApi::class.java)
        dao = mock(CurrencyPairDao::class.java)
        sharedPreferences = mock(SharedPreferences::class.java)
        mapper = CurrencyPairMapper()

        repository = CurrencyPairRepositoryImpl(api, dao, sharedPreferences, mapper)
    }

    @Test
    fun `getCurrencyPairList should return mapped DTO list`() = runBlocking {
        val jsonResponse = getJsonResponse()
        val expectedDtoList = CurrencyPairTestMockData.parseCurrencyPairListResponse(CurrencyPairTestMockData.getJsonResponseAsString())

        `when`(api.getCurrencyPairList()).thenReturn(jsonResponse)

        val result = repository.getCurrencyPairList()

        result.forEachIndexed { index, dto ->
            assertEquals(expectedDtoList[index].currencyPairAbbreviations, dto.currencyPairAbbreviations)
            assertEquals(expectedDtoList[index].currencyPairFullNames, dto.currencyPairFullNames)
        }
    }

    @Test
    fun `updateLastFetchDate should store current time`() = runBlocking {
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putLong(anyString(), anyLong())).thenReturn(editor)

        repository.updateLastFetchDate()

        verify(editor).putLong(eq("last_fetch_date"), anyLong())
        verify(editor).apply()
    }

    @Test
    fun `getLastFetchDate should return stored time`() = runBlocking {
        `when`(sharedPreferences.getLong("last_fetch_date", 0L)).thenReturn(1112223334445L)

        val result = repository.getLastFetchDate()

        assertEquals(1112223334445L, result)
    }

    @Test
    fun `saveCurrencyPairsToDatabase should map and save entities`() = runBlocking {
        val dtoList = CurrencyPairTestMockData.parseCurrencyPairListResponse(CurrencyPairTestMockData.getJsonResponseAsString())
        val entityList = dtoList.map { dto ->
            CurrencyPairEntity(
                id = dto.currencyPairAbbreviations,
                currencyPairAbbreviations = dto.currencyPairAbbreviations,
                currencyPairFullNames = dto.currencyPairFullNames
            )
        }
        `when`(mapper.fromDtoToEntityList(dtoList)).thenReturn(entityList)

        repository.saveCurrencyPairsToDatabase(dtoList)

        verify(dao).insertCurrencyPairs(entityList)
    }

    @Test
    fun `getCurrencyPairsFromDatabase should return entities`() = runBlocking {
        val entityList = CurrencyPairTestMockData.parseCurrencyPairListResponse(
            CurrencyPairTestMockData.getJsonResponseAsString())
            .map { dto ->
            CurrencyPairEntity(
                id = dto.currencyPairAbbreviations,
                currencyPairAbbreviations = dto.currencyPairAbbreviations,
                currencyPairFullNames = dto.currencyPairFullNames
            )
        }
        `when`(dao.getAllCurrencyPairs()).thenReturn(entityList)

        val result = repository.getCurrencyPairsFromDatabase()

        result.forEachIndexed { index, entity ->
            assertEquals(entityList[index].currencyPairAbbreviations, entity.currencyPairAbbreviations)
            assertEquals(entityList[index].currencyPairFullNames, entity.currencyPairFullNames)
        }
    }

}