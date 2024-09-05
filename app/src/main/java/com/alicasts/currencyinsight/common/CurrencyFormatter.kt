package com.alicasts.currencyinsight.common

import java.text.DecimalFormat
import kotlin.math.round

object CurrencyFormatter {

    fun calculateBaseCurrencyValue(targetValue: Double, bidValue: String): String {
        val bid = bidValue.toDoubleOrNull() ?: 0.0
        val result = targetValue * bid
        val formattedResult = if (result >= 1000) {
            round(result * 100) / 100
        } else {
            result
        }

        return if (formattedResult % 1.0 == 0.0) {
            formatLargeNumber(formattedResult)
        } else {
            formatLargeNumber(formattedResult)
        }
    }

    fun calculateTargetCurrencyValue(baseValue: Double, bidValue: String): String {
        val bid = bidValue.toDoubleOrNull() ?: 1.0
        val result = baseValue / bid

        return if (result >= 1000) {
            (round(result * 100) / 100).toString()
        } else {
            formatSmallNumber(result)
        }
    }

    private fun formatSmallNumber(value: Double): String {
        val df = DecimalFormat("#.########")
        return df.format(value)
    }

    private fun formatLargeNumber(value: Double): String {
        val df = DecimalFormat("#,###.##")
        return df.format(value)
    }
}