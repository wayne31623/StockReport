package com.example.stockreport.model

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.stockreport.MyApplication
import java.io.IOException
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File

class AssetsFileUtil {
    companion object {
        fun getUiStateData(): List<StockData> {
            val context = MyApplication.mContext
            val jsonStringBWIBBU_ALL = readJsonStringFromAssets(context, "BWIBBU_ALL.json")
            val jsonStringSTOCK_DAY_ALL = readJsonStringFromAssets(context, "STOCK_DAY_ALL.json")
            val jsonStringSTOCK_DAY_AVG_ALL = readJsonStringFromAssets(context, "STOCK_DAY_AVG_ALL.json")
            val result = mergeJsonData(listOf(jsonStringBWIBBU_ALL, jsonStringSTOCK_DAY_ALL, jsonStringSTOCK_DAY_AVG_ALL))
            return result.sortedByDescending { it.Code }
        }


        fun mergeJsonData(jsonFiles: List<String>): List<StockData> {
            val json = Json {
                prettyPrint = true
            }
            val mergedMap = mutableMapOf<String, StockData>()
            for (file in jsonFiles) {
                val stocks = json.decodeFromString<List<StockData>>(file)
                for (stock in stocks) {
                    val existing = mergedMap[stock.Code]
                    mergedMap[stock.Code] = if (existing != null) {
                        existing.copy(
                            OpeningPrice = stock.OpeningPrice ?: existing.OpeningPrice,
                            ClosingPrice = stock.ClosingPrice ?: existing.ClosingPrice,
                            HighestPrice = stock.HighestPrice ?: existing.HighestPrice,
                            LowestPrice = stock.LowestPrice ?: existing.LowestPrice,
                            Change = stock.Change ?: existing.Change,
                            MonthlyAveragePrice = stock.MonthlyAveragePrice ?: existing.MonthlyAveragePrice,
                            Transaction = stock.Transaction ?: existing.Transaction,
                            TradeVolume = stock.TradeVolume ?: existing.TradeVolume,
                            TradeValue = stock.TradeValue ?: existing.TradeValue,
                            PEratio = stock.PEratio ?: existing.PEratio,
                            DividendYield = stock.DividendYield ?: existing.DividendYield,
                            PBratio = stock.PBratio ?: existing.PBratio
                        )
                    } else {
                        stock
                    }
                }
            }
            return mergedMap.values.toList()
        }

        fun readJsonStringFromAssets(context: Context, fileName: String): String {
            try {
                val inputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                return String(buffer, Charsets.UTF_8)
            }
            catch (e: IOException) {
                throw IOException("無法讀取檔案 $fileName", e)
            }
        }
    }
}