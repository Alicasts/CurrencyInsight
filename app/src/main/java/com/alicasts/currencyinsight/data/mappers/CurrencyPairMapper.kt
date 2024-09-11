package com.alicasts.currencyinsight.data.mappers

import com.alicasts.currencyinsight.data.database.entities.CurrencyPairListEntity
import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel
import com.google.gson.JsonObject

class CurrencyPairMapper {
    fun fromDtoToModelList(items: List<CurrencyPairListItemDto>): List<CurrencyPairListItemModel> {
        return items.map { it.toModel() }
    }

    fun fromEntityToModelList(entities: List<CurrencyPairListEntity>): List<CurrencyPairListItemModel> {
        return entities.map { it.toModel() }
    }

    fun fromDtoToEntityList(items: List<CurrencyPairListItemDto>): List<CurrencyPairListEntity> {
        return items.map { it.toEntity() }
    }

    fun parseCurrencyPairListResponse(response: JsonObject): List<CurrencyPairListItemDto> {
        val currencyPairList = response.entrySet().map { (key, value) ->
            CurrencyPairListItemDto(key, value.asString)
        }
        return currencyPairList
    }

    private fun CurrencyPairListItemDto.toModel(): CurrencyPairListItemModel {
        return CurrencyPairListItemModel(
            currencyPairAbbreviations = this.currencyPairAbbreviations,
            currencyPairFullNames = this.currencyPairFullNames
        )
    }

    private fun CurrencyPairListEntity.toModel(): CurrencyPairListItemModel {
        return CurrencyPairListItemModel(
            currencyPairAbbreviations = this.currencyPairAbbreviations,
            currencyPairFullNames = this.currencyPairFullNames
        )
    }

    private fun CurrencyPairListItemDto.toEntity(): CurrencyPairListEntity {
        return CurrencyPairListEntity(
            currencyPairAbbreviations = this.currencyPairAbbreviations,
            currencyPairFullNames = this.currencyPairFullNames
        )
    }
}