package com.alicasts.currencyinsight.data.mappers

import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel

class CurrencyPairMapper {
    fun fromDtoToModelList(items: List<CurrencyPairListItemDto>): List<CurrencyPairListItemModel> {
        return items.map { it.toCurrencyPairListItemModel() }
    }
}