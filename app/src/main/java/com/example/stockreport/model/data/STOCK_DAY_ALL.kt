package com.example.stockreport.model.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class STOCK_DAY_ALL(
    @SerialName("Code")
    val Code: String,
    @SerialName("Name")
    val Name: String? = null,
    @SerialName("TradeVolume")
    val TradeVolume: String? = null,
    @SerialName("TradeValue")
    val TradeValue: String? = null,
    @SerialName("OpeningPrice")
    val OpeningPrice: String? = null,
    @SerialName("HighestPrice")
    val HighestPrice: String? = null,
    @SerialName("LowestPrice")
    val LowestPrice: String? = null,
    @SerialName("ClosingPrice")
    val ClosingPrice: String? = null,
    @SerialName("Change")
    val Change: String? = null,
    @SerialName("Transaction")
    val Transaction: String? = null
)
