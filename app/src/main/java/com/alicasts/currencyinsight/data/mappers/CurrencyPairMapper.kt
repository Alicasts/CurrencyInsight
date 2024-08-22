package com.alicasts.currencyinsight.data.mappers

import com.alicasts.currencyinsight.data.database.CurrencyPairEntity
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.model.currency_comparsion.CurrencyHistoricalData
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel

class CurrencyPairMapper {
    fun fromDtoToModelList(items: List<CurrencyPairListItemDto>): List<CurrencyPairListItemModel> {
        return items.map { it.toModel() }
    }

    fun fromEntityToModelList(entities: List<CurrencyPairEntity>): List<CurrencyPairListItemModel> {
        return entities.map { it.toModel() }
    }

    fun fromDtoToEntityList(items: List<CurrencyPairListItemDto>): List<CurrencyPairEntity> {
        return items.map { it.toEntity() }
    }

    fun CurrencyPairListItemDto.toModel(): CurrencyPairListItemModel {
        return CurrencyPairListItemModel(
            currencyPairAbbreviations = this.currencyPairAbbreviations,
            currencyPairFullNames = this.currencyPairFullNames
        )
    }

    fun CurrencyPairEntity.toModel(): CurrencyPairListItemModel {
        return CurrencyPairListItemModel(
            currencyPairAbbreviations = this.currencyPairAbbreviations,
            currencyPairFullNames = this.currencyPairFullNames
        )
    }

    fun CurrencyPairListItemDto.toEntity(): CurrencyPairEntity {
        return CurrencyPairEntity(
            id = this.currencyPairAbbreviations,
            currencyPairAbbreviations = this.currencyPairAbbreviations,
            currencyPairFullNames = this.currencyPairFullNames
        )
    }
}