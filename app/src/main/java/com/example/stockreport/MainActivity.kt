package com.example.stockreport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.stockreport.ui.screen.MainScreen
import com.example.stockreport.ui.theme.StockReportTheme
import com.example.stockreport.viewmodel.MainViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: MainViewModel by viewModels()
        super.onCreate(savedInstanceState)
        setContent {
            StockReportTheme {
                MainScreen(viewModel)
            }
        }
    }
}

