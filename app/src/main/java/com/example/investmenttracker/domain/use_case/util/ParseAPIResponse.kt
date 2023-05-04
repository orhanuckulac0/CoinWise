package com.example.investmenttracker.domain.use_case.util

import com.example.investmenttracker.data.model.CoinModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject

fun parseMultipleCoinsResponseUtil(data: JsonObject): MutableList<CoinModel> {
    val newTokensTempHolder = mutableListOf<CoinModel>()
    try {
        val jsonObject = JSONObject(data.toString())
        val dataObject = jsonObject.getJSONObject("data")

        val keys = dataObject.keys()
        while (keys.hasNext()) {
            val key = keys.next() as String
            val rowObject = dataObject.getJSONObject(key)
            val quoteObject = rowObject.getJSONObject("quote")
            val usdObject = quoteObject.getJSONObject("USD")

            val coin = CoinModel(
                id = rowObject.getInt("id"), // cmcId
                cmcId = rowObject.getInt("id"), // cmcId
                name = rowObject.getString("name"),
                slug = rowObject.getString("slug"),
                symbol = rowObject.getString("symbol"),
                price = usdObject.getString("price").toDouble(),
                marketCap =  usdObject.getString("market_cap").toDouble(),
                percentChange24h=  usdObject.getString("percent_change_24h").toDouble(),
                totalTokenHeldAmount = 0.0,
                totalInvestmentAmount = 0.0,
                totalInvestmentWorth = 0.0,
                Constants.USD
            )
            newTokensTempHolder.add(coin)
        }
        return newTokensTempHolder

    } catch (_: JSONException) { }
    return newTokensTempHolder
}

fun parseSymbolResponseUtil(response: JsonArray?): MutableList<CoinModel>{
    val newTokensTempHolder = mutableListOf<CoinModel>()

    if (response != null){
        try {
            for (c in response.asJsonArray){
                val coin = CoinModel(
                    id = c.asJsonObject.get("id").toString().toInt(),
                    cmcId = c.asJsonObject.get("id").toString().toInt(),
                    name = formatCoinNameText(c.asJsonObject.get("name").toString()),
                    slug = c.asJsonObject.get("slug").toString(),
                    symbol = c.asJsonObject.get("symbol").toString(),
                    price = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("price").toString().toDouble(),
                    marketCap = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("market_cap").toString().toDouble(),
                    percentChange24h = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_24h").toString().toDouble(),
                    totalTokenHeldAmount = 0.toDouble(),
                    totalInvestmentAmount = 0.toDouble(),
                    totalInvestmentWorth =0.toDouble(),
                    Constants.USD
                )
                newTokensTempHolder.add(coin)
            }
        } catch (_: JSONException) { }
    }
    return newTokensTempHolder
}

fun parseSlugResponseUtil(result: JSONObject): CoinModel? {
    try {
        val firstKey = result.keys().next()
        val resultCoinObject = result.getJSONObject(firstKey)

        val quoteObject = resultCoinObject.getJSONObject("quote")
        val usdObject = quoteObject.getJSONObject("USD")

        return CoinModel(
            id = resultCoinObject.getInt("id"),
            cmcId = resultCoinObject.getInt("id"),
            name = formatCoinNameText(resultCoinObject.get("name").toString()),
            slug = resultCoinObject.get("slug").toString(),
            symbol = resultCoinObject.get("symbol").toString(),
            price = usdObject.get("price").toString().toDouble(),
            marketCap = usdObject.get("market_cap").toString().toDouble(),
            percentChange24h = usdObject.get("percent_change_24h").toString().toDouble(),
            totalTokenHeldAmount = 0.toDouble(),
            totalInvestmentAmount = 0.toDouble(),
            totalInvestmentWorth = 0.toDouble(),
            Constants.USD
        )
    } catch (_: JSONException) { }
    return null
}

fun parseCurrencyAPIResponse(result: JsonObject): Map<String, Float>? {
    try {
        val exchangeRate = result.get("conversion_rates")
        val ratesObj = exchangeRate.asJsonObject
        val currencyCodes = listOf("EUR", "SGD", "CAD", "AUD", "TRY", "NZD")

        val rates = currencyCodes.associateWith { code ->
            ratesObj[code].asFloat
        }

        return rates
    } catch (_: JSONException) { }
    return null
}

fun formatCoinNameText(symbol: String): String {
    val regex = Regex("[^A-Za-z0-9 ]")
    return regex.replace(symbol, "")
}