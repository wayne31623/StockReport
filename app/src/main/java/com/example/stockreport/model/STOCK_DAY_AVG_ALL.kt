package com.example.stockreport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class STOCK_DAY_AVG_ALL(
    @SerialName("Code")
    val Code: String,
    @SerialName("Name")
    val Name: String? = null,
    @SerialName("ClosingPrice")
    val ClosingPrice: String? = null,
    @SerialName("MonthlyAveragePrice")
    val MonthlyAveragePrice: String? = null
)
