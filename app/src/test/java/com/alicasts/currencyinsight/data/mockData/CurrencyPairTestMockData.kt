package com.alicasts.currencyinsight.data.mockData

import com.alicasts.currencyinsight.data.dto.CurrencyPairListItemDto
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object CurrencyPairTestMockData {

    private val jsonParser = JsonParser()

    private val jsonObject = generateResponse()


    fun getJsonResponseAsString(): String {
        return jsonObject.toString()
    }


    fun parseCurrencyPairListResponse(jsonString: String): List<CurrencyPairListItemDto> {
        val jsonObject = jsonParser.parse(jsonString).asJsonObject
        return jsonObject.entrySet().map { (key, value) ->
            CurrencyPairListItemDto(key, value.asString)
        }
    }

    private fun generateResponse(): JsonObject {
        val jsonObject by lazy {
            JsonObject().apply {
                addProperty("USD-BRL", "Dólar Americano/Real Brasileiro")
                addProperty("USD-BRLT", "Dólar Americano/Real Brasileiro Turismo")
                addProperty("CAD-BRL", "Dólar Canadense/Real Brasileiro")
                addProperty("EUR-BRL", "Euro/Real Brasileiro")
                addProperty("GBP-BRL", "Libra Esterlina/Real Brasileiro")
                addProperty("ARS-BRL", "Peso Argentino/Real Brasileiro")
                addProperty("BTC-BRL", "Bitcoin/Real Brasileiro")
                addProperty("LTC-BRL", "Litecoin/Real Brasileiro")
                addProperty("JPY-BRL", "Iene Japonês/Real Brasileiro")
                addProperty("CHF-BRL", "Franco Suíço/Real Brasileiro")
                addProperty("AUD-BRL", "Dólar Australiano/Real Brasileiro")
                addProperty("CNY-BRL", "Yuan Chinês/Real Brasileiro")
                addProperty("ILS-BRL", "Novo Shekel Israelense/Real Brasileiro")
                addProperty("ETH-BRL", "Ethereum/Real Brasileiro")
                addProperty("XRP-BRL", "XRP/Real Brasileiro")
                addProperty("EUR-USD", "Euro/Dólar Americano")
                addProperty("CAD-USD", "Dólar Canadense/Dólar Americano")
                addProperty("GBP-USD", "Libra Esterlina/Dólar Americano")
                addProperty("ARS-USD", "Peso Argentino/Dólar Americano")
                addProperty("JPY-USD", "Iene Japonês/Dólar Americano")
                addProperty("CHF-USD", "Franco Suíço/Dólar Americano")
            }
        }
        return jsonObject
    }

}