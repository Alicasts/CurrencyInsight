package com.alicasts.currencyinsight.data.mockData

import com.alicasts.currencyinsight.data.dto.CurrencyComparisonDetailDto
import com.alicasts.currencyinsight.data.dto.CurrencyHistoricalDataDto
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyComparisonDetails
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyHistoricalData
import com.google.gson.JsonArray
import com.google.gson.JsonParser

object CurrencyComparisonWithDetailsTestMockData {

    private const val jsonString = """
        [
            {"code":"USD","codein":"BRL","name":"Dólar Americano/Real Brasileiro","high":"5.6061","low":"5.4716","varBid":"-0.0785","pctChange":"-1.41","bid":"5.5037","ask":"5.506","timestamp":"1724434780","create_date":"2024-08-23 14:39:40"},
            {"high":"5.5936","low":"5.5844","varBid":"0.0082","pctChange":"0.15","bid":"5.5911","ask":"5.5921","timestamp":"1724361875"},
            {"high":"5.4878","low":"5.4811","varBid":"-0.0022","pctChange":"-0.04","bid":"5.4831","ask":"5.4841","timestamp":"1724275745"},
            {"high":"5.4855","low":"5.4774","varBid":"0.001","pctChange":"0.02","bid":"5.4785","ask":"5.4795","timestamp":"1724189348"},
            {"high":"5.4131","low":"5.407","varBid":"-0.0048","pctChange":"-0.09","bid":"5.4068","ask":"5.4078","timestamp":"1724102967"},
            {"high":"5.4878","low":"5.4365","varBid":"-0.0033","pctChange":"-0.06","bid":"5.4714","ask":"5.4727","timestamp":"1724008520"},
            {"high":"5.4948","low":"5.4375","varBid":"-0.0069","pctChange":"-0.13","bid":"5.4753","ask":"5.4763","timestamp":"1723841989"},
            {"high":"5.4948","low":"5.4375","varBid":"-0.0096","pctChange":"-0.18","bid":"5.4728","ask":"5.4733","timestamp":"1723841944"},
            {"high":"5.4878","low":"5.48","varBid":"0.0015","pctChange":"0.03","bid":"5.4837","ask":"5.4847","timestamp":"1723757395"},
            {"high":"5.4756","low":"5.4689","varBid":"-0.0031","pctChange":"-0.06","bid":"5.4695","ask":"5.4703","timestamp":"1723670982"},
            {"high":"5.4573","low":"5.4499","varBid":"0.0025","pctChange":"0.05","bid":"5.4558","ask":"5.4568","timestamp":"1723584568"},
            {"high":"5.5018","low":"5.4923","varBid":"-0.0041","pctChange":"-0.07","bid":"5.4929","ask":"5.4937","timestamp":"1723501682"},
            {"high":"5.5535","low":"5.4917","varBid":"0.0013","pctChange":"0.02","bid":"5.5064","ask":"5.5091","timestamp":"1723409980"},
            {"high":"5.5535","low":"5.4917","varBid":"-0.0433","pctChange":"-0.78","bid":"5.5054","ask":"5.5066","timestamp":"1723237194"},
            {"high":"5.5535","low":"5.4917","varBid":"-0.0405","pctChange":"-0.73","bid":"5.5084","ask":"5.5091","timestamp":"1723237140"}
        ]
    """

    fun getCurrencyComparisonJsonArray(): JsonArray {
        val jsonParser = JsonParser()
        return jsonParser.parse(jsonString).asJsonArray
    }

    fun parseCurrencyComparisonDetailsResponse(response: JsonArray): CurrencyComparisonDetailDto {
        val historicalData = response.drop(1).map { element ->
            val jsonObject = element.asJsonObject
            CurrencyHistoricalDataDto(
                high = jsonObject["high"].asString,
                low = jsonObject["low"].asString,
                varBid = jsonObject["varBid"].asString,
                pctChange = jsonObject["pctChange"].asString,
                bid = jsonObject["bid"].asString,
                ask = jsonObject["ask"].asString,
                timestamp = jsonObject["timestamp"].asString
            )
        }

        val jsonObject = response[0].asJsonObject

        return CurrencyComparisonDetailDto(
            code = jsonObject["code"]?.asString ?: "",
            codein = jsonObject["codein"]?.asString ?: "",
            name = jsonObject["name"]?.asString ?: "",
            high = jsonObject["high"].asString,
            low = jsonObject["low"].asString,
            varBid = jsonObject["varBid"].asString,
            pctChange = jsonObject["pctChange"].asString,
            bid = jsonObject["bid"].asString,
            ask = jsonObject["ask"].asString,
            timestamp = jsonObject["timestamp"].asString,
            createDate = jsonObject["create_date"]?.asString ?: "",
            historicalData = historicalData
        )
    }

    fun returnMockDto(): CurrencyComparisonDetailDto {
        val remoteDto = CurrencyComparisonDetailDto(
            code = "USD",
            codein = "BRL",
            name = "Dólar Americano/Real Brasileiro",
            high = "5.6061",
            low = "5.4716",
            varBid = "-0.0785",
            pctChange = "-1.41",
            bid = "5.5037",
            ask = "5.506",
            timestamp = "1724434780",
            createDate = "2024-08-23 14:39:40",
            historicalData = listOf(
                CurrencyHistoricalDataDto(
                    high = "5.5936",
                    low = "5.5844",
                    varBid = "0.0082",
                    pctChange = "0.15",
                    bid = "5.5911",
                    ask = "5.5921",
                    timestamp = "1724361875"
                )
            )
        )
        return remoteDto
    }

    fun returnMockCurrencyComparisonDetails(): CurrencyComparisonDetails {
        val outdatedLocalData = CurrencyComparisonDetails(
            code = "USD",
            codein = "BRL",
            name = "Dólar Americano/Real Brasileiro",
            high = "5.6061",
            low = "5.4716",
            varBid = "-0.0785",
            pctChange = "-1.41",
            bid = "5.5037",
            ask = "5.506",
            timestamp = "1724434780",
            createDate = "2024-08-23 08:00:00",
            historicalData = listOf(
                CurrencyHistoricalData(
                    high = "5.5936",
                    low = "5.5844",
                    varBid = "0.0082",
                    pctChange = "0.15",
                    bid = "5.5911",
                    ask = "5.5921",
                    timestamp = "1724361875"
                )
            )
        )
        return outdatedLocalData
    }
}