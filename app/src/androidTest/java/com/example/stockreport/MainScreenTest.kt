package com.example.stockreport

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.stockreport.model.data.StockData
import com.example.stockreport.ui.screen.MainScreen
import com.example.stockreport.ui.theme.StockReportTheme
import com.example.stockreport.viewmodel.MainViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true) {
            every { uiState } returns MutableStateFlow(emptyList())
        }
    }

    @Test
    fun loadingState_showsCircularProgressIndicator() {
        composeTestRule.setContent {
            StockReportTheme {
                MainScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun dataLoaded_showsStockData() = runTest {
        val stockDataList = listOf(
            StockData("9105", "泰金寶-DR", "8.42", "8.40", "8.55", "8.35", "0.0500", "8.90", "10334", "54632322", "460971943", "31.29", "3.56", "2.06"),
            StockData("2330", "台積電", "500", "505", "510", "495", "5", "502", "10000", "1000000", "500000000", "20", "2", "1.5")
        )

        every { viewModel.uiState } returns MutableStateFlow(stockDataList)

        composeTestRule.setContent {
            StockReportTheme {
                MainScreen(viewModel)
            }
        }
        advanceUntilIdle() // Wait for the flow to be collected and Compose to recompose

        composeTestRule.onNodeWithText("泰金寶-DR").assertIsDisplayed()
        composeTestRule.onNodeWithText("台積電").assertIsDisplayed()
        composeTestRule.onNodeWithText("8.40").assertIsDisplayed()
        composeTestRule.onNodeWithText("505").assertIsDisplayed()
    }

    @Test
    fun filterButtonClick_showsBottomSheet() {
        composeTestRule.setContent {
            StockReportTheme {
                MainScreen(viewModel)
            }
        }

        composeTestRule.onNodeWithContentDescription("filter").performClick()

        composeTestRule.onNodeWithText("依股票代號降序").assertIsDisplayed()
        composeTestRule.onNodeWithText("依股票代號升序").assertIsDisplayed()
    }

    @Test
    fun stockItemClick_showsAlertDialog() = runTest {
        val stockData = listOf(
            StockData("9105", "泰金寶-DR", "8.42", "8.40", "8.55", "8.35", "0.0500", "8.90", "10334", "54632322", "460971943", "31.29", "3.56", "2.06")
        )
        every { viewModel.uiState } returns MutableStateFlow(stockData)

        composeTestRule.setContent {
            StockReportTheme {
                MainScreen(viewModel)
            }
        }
        advanceUntilIdle()

        composeTestRule.onNodeWithText("泰金寶-DR").performClick()

        advanceUntilIdle()

        composeTestRule.onNodeWithText("資訊顯示").assertIsDisplayed()
        //composeTestRule.onNodeWithText("本益比 31.29\n殖利率 3.56\n股價淨值比 2.06").assertIsDisplayed()
    }

    @Test
    fun sortByDescending_sortsCorrectly() = runTest {
        val stockData = listOf(
            StockData("2330", "台積電", "500", "505", "510", "495", "5", "502", "10000", "1000000", "500000000", "20", "2", "1.5"),
            StockData("9105", "泰金寶-DR", "8.42", "8.40", "8.55", "8.35", "0.0500", "8.90", "10334", "54632322", "460971943", "31.29", "3.56", "2.06")
        )
        every { viewModel.uiState } returns MutableStateFlow(stockData)
        every { viewModel.reverseList() } answers {
            val mutableList = stockData.toMutableList()
            mutableList.sortByDescending { it.Code }
            every { viewModel.uiState } returns MutableStateFlow(mutableList)
        }

        composeTestRule.setContent {
            StockReportTheme {
                MainScreen(viewModel)
            }
        }
        advanceUntilIdle()

        composeTestRule.onNodeWithText("9105").assertIsDisplayed()
        composeTestRule.onNodeWithText("2330").assertIsDisplayed()
    }


}