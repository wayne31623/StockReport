package com.example.stockreport.model.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BWIBBU_ALL(
    @SerialName("Code")
    val Code: String,
    @SerialName("Name")
    val Name: String? = null,
    @SerialName("PEratio")
    val PEratio: String? = null,
    @SerialName("DividendYield")
    val DividendYield: String? = null,
    @SerialName("PBratio")
    val PBratio: String? = null
)
