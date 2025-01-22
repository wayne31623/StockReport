package com.example.stockreport.network

import com.example.stockreport.model.BWIBBU_ALL
import com.example.stockreport.model.STOCK_DAY_ALL
import com.example.stockreport.model.STOCK_DAY_AVG_ALL
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://openapi.twse.com.tw/v1/"

/**
 * Retrofit service object for creating api calls
 */
interface TWSEApiService {
    @GET("exchangeReport/BWIBBU_ALL")
    suspend fun getBAData(): List<BWIBBU_ALL>

    @GET("exchangeReport/STOCK_DAY_AVG_ALL")
    suspend fun getSDAAData(): List<STOCK_DAY_AVG_ALL>

    @GET("exchangeReport/STOCK_DAY_ALL")
    suspend fun getSDAData(): List<STOCK_DAY_ALL>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object TWSEApi {
    val apiService: TWSEApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TWSEApiService::class.java)
    }
}