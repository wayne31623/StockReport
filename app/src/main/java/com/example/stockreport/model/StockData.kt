package com.example.stockreport.model

import kotlinx.serialization.Serializable

@Serializable
data class StockData(
    var Code: String,
    var Name: String,
    var OpeningPrice: String? = null,
    var ClosingPrice: String? = null,
    var HighestPrice: String? = null,
    var LowestPrice: String? = null,
    var Change: String? = null,
    var MonthlyAveragePrice: String? = null,
    var Transaction: String? = null,
    var TradeVolume: String? = null,
    var TradeValue: String? = null,
    var PEratio: String? = null,
    var DividendYield: String? = null,
    var PBratio: String? = null
)
