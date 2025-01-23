package com.example.stockreport.model

import com.example.stockreport.model.data.BWIBBU_ALL
import com.example.stockreport.model.data.STOCK_DAY_ALL
import com.example.stockreport.model.data.STOCK_DAY_AVG_ALL
import com.example.stockreport.model.data.StockData
import com.example.stockreport.network.TWSEApiService

class StockDataRepository(private val apiService: TWSEApiService) {

    suspend fun fetchAndMergeData(): List<StockData> {
        try{
            // call api
            val baData = apiService.getBAData()
            val sdaaData = apiService.getSDAAData()
            val sdaData = apiService.getSDAData()
            // merge data
            val mergedData = mergeLists(baData, sdaaData, sdaData)
            return mergedData.sortedByDescending { it.Code }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    fun mergeLists(list1: List<BWIBBU_ALL>, list2: List<STOCK_DAY_AVG_ALL>, list3: List<STOCK_DAY_ALL>): List<StockData> {
        // 使用Map，以code作為鍵，暫存合併的結果
        val mergedMap = mutableMapOf<String, StockData>()

        // 合併list1
        list1.forEach { item ->
            mergedMap[item.Code] = mergedMap[item.Code]?.copy(
                Name = item.Name,
                PEratio = item.PEratio,
                DividendYield = item.DividendYield,
                PBratio = item.PBratio
            ) ?: StockData(
                Code = item.Code,
                Name = item.Name,
                PEratio = item.PEratio,
                DividendYield = item.DividendYield,
                PBratio = item.PBratio
            )
        }

        // 合併list2
        list2.forEach { item ->
            mergedMap[item.Code] = mergedMap[item.Code]?.copy(
                Name = item.Name,
                ClosingPrice = item.ClosingPrice,
                MonthlyAveragePrice = item.MonthlyAveragePrice
            ) ?: StockData(
                Code = item.Code,
                Name = item.Name,
                ClosingPrice = item.ClosingPrice,
                MonthlyAveragePrice = item.MonthlyAveragePrice
            )
        }

        // 合併list3
        list3.forEach { item ->
            mergedMap[item.Code] = mergedMap[item.Code]?.copy(
                Name = item.Name,
                TradeVolume = item.TradeVolume,
                TradeValue = item.TradeValue,
                OpeningPrice = item.OpeningPrice,
                HighestPrice = item.HighestPrice,
                LowestPrice = item.LowestPrice,
                ClosingPrice = item.ClosingPrice,
                Change = item.Change,
                Transaction = item.Transaction
            ) ?: StockData(
                Code = item.Code,
                Name = item.Name,
                TradeVolume = item.TradeVolume,
                TradeValue = item.TradeValue,
                OpeningPrice = item.OpeningPrice,
                HighestPrice = item.HighestPrice,
                LowestPrice = item.LowestPrice,
                ClosingPrice = item.ClosingPrice,
                Change = item.Change,
                Transaction = item.Transaction
            )
        }

        // 將Map轉換為List
        return mergedMap.values.toList()
    }
}