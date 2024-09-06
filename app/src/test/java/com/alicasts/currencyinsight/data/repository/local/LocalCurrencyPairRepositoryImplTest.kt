package com.alicasts.currencyinsight.data.repository.local

import android.content.SharedPreferences
import com.alicasts.currencyinsight.common.Constants
import com.alicasts.currencyinsight.data.database.dao.CurrencyComparisonDao
import com.alicasts.currencyinsight.data.database.dao.CurrencyPairListDao
import com.alicasts.currencyinsight.data.database.entities.CurrencyPairListEntity
import com.alicasts.currencyinsight.data.mappers.CurrencyComparisonMapper
import com.alicasts.currencyinsight.data.mappers.CurrencyPairMapper
import com.alicasts.currencyinsight.data.mockData.CurrencyComparisonWithDetailsTestMockData
import com.alicasts.currencyinsight.data.mockData.CurrencyPairTestMockData
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

class LocalCurrencyPairRepositoryImplTest {

    private lateinit var dao: CurrencyPairListDao
    private lateinit var currencyComparisonDao: CurrencyComparisonDao
    private lateinit var currencyComparisonMapper: CurrencyComparisonMapper
    private lateinit var mapper: CurrencyPairMapper
    private lateinit var repository: LocalCurrencyPairRepositoryImpl
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        dao = mock(CurrencyPairListDao::class.java)
        sharedPreferences = mock(SharedPreferences::class.java)
        mapper = CurrencyPairMapper()
        currencyComparisonDao = mock(CurrencyComparisonDao::class.java)
        currencyComparisonMapper = CurrencyComparisonMapper()

        repository = LocalCurrencyPairRepositoryImpl(dao, sharedPreferences, mapper, currencyComparisonDao, currencyComparisonMapper)
    }

    @Test
    fun `persistUpdatedList should update last fetch date and save entities`() = runBlocking {
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putLong(anyString(), anyLong())).thenReturn(editor)

        val dtoList = CurrencyPairTestMockData.parseCurrencyPairListResponse(CurrencyPairTestMockData.getJsonResponseAsString())
        val entityList = dtoList.map { dto ->
            CurrencyPairListEntity(
                currencyPairAbbreviations = dto.currencyPairAbbreviations,
                currencyPairFullNames = dto.currencyPairFullNames
            )
        }

        repository = LocalCurrencyPairRepositoryImpl(dao, sharedPreferences, mapper, currencyComparisonDao, currencyComparisonMapper)

        repository.persistUpdatedList(dtoList)

        verify(editor).putLong(eq("last_fetch_date"), anyLong())
        verify(editor).apply()
        verify(dao).insertCurrencyPairs(entityList)
    }

    @Test
    fun `getCurrencyPairsModelList should return mapped entity list`() = runBlocking {
        val entityList = CurrencyPairTestMockData.parseCurrencyPairListResponse(
            CurrencyPairTestMockData.getJsonResponseAsString()
        ).map { dto ->
            CurrencyPairListEntity(
                currencyPairAbbreviations = dto.currencyPairAbbreviations,
                currencyPairFullNames = dto.currencyPairFullNames
            )
        }
        `when`(dao.getAllCurrencyPairs()).thenReturn(entityList)

        val result = repository.getCurrencyPairsModelList()

        result.forEachIndexed { index, model ->
            assertEquals(entityList[index].currencyPairAbbreviations, model.currencyPairAbbreviations)
            assertEquals(entityList[index].currencyPairFullNames, model.currencyPairFullNames)
        }
    }

    @Test
    fun `getLastFetchDate should return stored time`() = runBlocking {
        `when`(sharedPreferences.getLong(Constants.LAST_FETCH_DATE_KEY, 0L)).thenReturn(1112223334445L)

        val result = repository.getLastFetchDate()

        assertEquals(1112223334445L, result)
    }

    @Test
    fun `persistComparisonDetails should save comparison and historical data`() = runBlocking {
        val detailDto = CurrencyComparisonWithDetailsTestMockData.returnMockDto()
        val comparisonEntity = currencyComparisonMapper.mapToEntity(detailDto)
        val historicalDataEntities = currencyComparisonMapper.mapToHistoricalEntities(detailDto.historicalData, comparisonEntity.comparisonCode)

        repository.persistComparisonDetails(detailDto)

        verify(currencyComparisonDao).insertCurrencyComparison(comparisonEntity)
        verify(currencyComparisonDao).deleteHistoricalData(comparisonEntity.comparisonCode)
        verify(currencyComparisonDao).insertHistoricalData(historicalDataEntities)
    }

    @Test
    fun `clearSelectedHistoricalData should delete historical data`() = runBlocking {
        val comparisonCode = "USD_BRL"

        repository.clearSelectedHistoricalData(comparisonCode)

        verify(currencyComparisonDao).deleteHistoricalData(comparisonCode)
    }
}