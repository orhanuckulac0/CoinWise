package com.example.investmenttracker.domain.use_case.coin

import android.util.Log
import com.example.investmenttracker.data.model.CoinModel
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject

class FormatAPIResponseUseCase {

    fun execute(data: JsonObject): MutableList<CoinModel> {
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

}