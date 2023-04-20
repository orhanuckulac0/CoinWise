package com.example.investmenttracker.domain.use_case.util

import android.util.Log
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

        // Iterate over the keys in the data object
        val keys = dataObject.keys()
        while (keys.hasNext()) {
            val key = keys.next() as String
            val rowObject = dataObject.getJSONObject(key)
            val quoteObject = rowObject.getJSONObject("quote")
            val usdObject = quoteObject.getJSONObject("USD")

            // Extract the desired data from the row object
            // create a model out if it
            val coin = CoinModel(
                id = rowObject.getInt("id"), // cmcId
                cmcId = rowObject.getInt("id"), // cmcId
                name = rowObject.getString("name"),
                slug = rowObject.getString("slug"),
                symbol = rowObject.getString("symbol"),
                price = usdObject.getString("price").toDouble(),
                marketCap =  usdObject.getString("market_cap").toDouble(),
                percentChange1h = usdObject.getString("percent_change_1h").toDouble(),
                percentChange24h=  usdObject.getString("percent_change_24h").toDouble(),
                percentChange7d=  usdObject.getString("percent_change_7d").toDouble(),
                percentChange30d=  usdObject.getString("percent_change_30d").toDouble(),
                totalTokenHeldAmount = 0.0,
                totalInvestmentAmount = 0.0,
                totalInvestmentWorth = 0.0
            )
            newTokensTempHolder.add(coin)
        }
        return newTokensTempHolder

    } catch (e: JSONException) {
        Log.e("MYTAG", "Error parsing JSON: ${e.message}")
    }
    return newTokensTempHolder
}

fun parseSymbolResponseUtil(response: JsonArray?): MutableList<CoinModel>{
    val newTokensTempHolder = mutableListOf<CoinModel>()

    try {
        for (c in response!!.asJsonArray){
            val coin = CoinModel(
                id = c.asJsonObject.get("id").toString().toInt(),
                cmcId = c.asJsonObject.get("id").toString().toInt(),
                name = formatCoinNameText(c.asJsonObject.get("name").toString()),
                slug = c.asJsonObject.get("slug").toString(),
                symbol = c.asJsonObject.get("symbol").toString(),
                price = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("price").toString().toDouble(),
                marketCap = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("market_cap").toString().toDouble(),
                percentChange1h = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_1h").toString().toDouble(),
                percentChange24h = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_24h").toString().toDouble(),
                percentChange7d = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_7d").toString().toDouble(),
                percentChange30d = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_30d").toString().toDouble(),
                totalTokenHeldAmount = 0.toDouble(),
                totalInvestmentAmount = 0.toDouble(),
                totalInvestmentWorth =0.toDouble()
            )
            newTokensTempHolder.add(coin)
        }
    } catch (e: java.lang.Exception) {
        Log.e("MYTAG", "Error parsing JSON: ${e.message}")
    }
    return newTokensTempHolder
}

fun parseSlugResponseUtil(result: JSONObject): CoinModel? {
    try {
        val firstKey = result.keys().next()  // use keys().next() because each firstKey is unique
        val resultCoinObject = result.getJSONObject(firstKey)
        Log.i("MYTAG"," resultCoinObject $resultCoinObject")

        // Extract the "USD" object from the "quote" object
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
            percentChange1h = usdObject.get("percent_change_1h").toString().toDouble(),
            percentChange24h = usdObject.get("percent_change_24h").toString().toDouble(),
            percentChange7d = usdObject.get("percent_change_7d").toString().toDouble(),
            percentChange30d = usdObject.get("percent_change_30d").toString().toDouble(),
            totalTokenHeldAmount = 0.toDouble(),
            totalInvestmentAmount = 0.toDouble(),
            totalInvestmentWorth = 0.toDouble()
        )
    } catch (e: java.lang.Exception) {
        Log.e("MYTAG", "Error parsing JSON: ${e.message}")
    }
    return null
}

fun parseCurrencyAPIResponse(result: JsonObject, resultCurrency: String): String? {
    try {
        val rateObj = result.getAsJsonObject("rates")
        val currencyObj = rateObj.getAsJsonObject(resultCurrency)
        return currencyObj.get("rate_for_amount").toString().replace("\"", "")
    }catch (e: java.lang.Exception){
        Log.e("MYTAG", "Error parsing JSON: ${e.message}")
    }
    return null
}

// to remove "" from coin symbols coming with api response
fun formatCoinNameText(symbol: String): String {
    val regex = Regex("[^A-Za-z0-9 ]")
    return regex.replace(symbol, "")
}