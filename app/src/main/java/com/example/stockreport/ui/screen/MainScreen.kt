package com.example.stockreport.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockreport.R
import com.example.stockreport.model.data.StockData
import com.example.stockreport.ui.theme.GhostWhite
import com.example.stockreport.ui.theme.StockReportTheme
import com.example.stockreport.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()                       // ui state
    val sheetState = rememberModalBottomSheetState()                        // bottom sheet state
    val scope = rememberCoroutineScope()                                    // coroutine scope
    var showBottomSheet by remember { mutableStateOf(false) }         // show bottom sheet
    var isDescending by remember { mutableStateOf(true) }             // descending order
    val selectedItem = remember { mutableStateOf<StockData?>(null) }  // selected item
    val darkTheme = isSystemInDarkTheme()                                   // show dark theme
    var cContainerColor = MaterialTheme.colorScheme.surfaceVariant          // card color
    var bsContainerColor = MaterialTheme.colorScheme.surface                // bottom sheet color
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if(!darkTheme) {
            cContainerColor = GhostWhite
            bsContainerColor = GhostWhite
        }
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            // filter button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clickable { showBottomSheet = true }
                        .size(48.dp)
                        .padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "filter"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (uiState.isEmpty()) {
                // show loading
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp).testTag("loading_indicator"),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
            else {
                // show data
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.size) { index ->
                        val item = uiState[index]
                        Card(
                            onClick = {
                                selectedItem.value = item
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = cContainerColor
                            ),
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
                        ) {
                            CardContent(item)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                selectedItem.value?.let { item ->
                    AlertDialog(
                        onDismissRequest = { selectedItem.value = null },
                        title = {
                            Text(text = "資訊顯示")
                        },
                        text = {
                            Text(text = "本益比 ${item.PEratio}\n殖利率 ${item.DividendYield}%\n股價淨值比 ${item.PBratio}")
                        },
                        confirmButton = {
                            Button(onClick = { selectedItem.value = null }) {
                                Text(text = "確定")
                            }
                        }
                    )
                }
            }
        }
        if (showBottomSheet) {
            // bottom sheet
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                containerColor = bsContainerColor
            ) {
                TextButton(
                    onClick = {
                        if(!isDescending) {
                            viewModel.reverseList()
                            isDescending = true
                        }
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "依股票代號降序", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                }
                TextButton(
                    onClick = {
                        if(isDescending) {
                            viewModel.reverseList()
                            isDescending = false
                        }
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "依股票代號升序", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Card content
 */
@Composable
fun CardContent(item: StockData) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val monthlyAveragePrice = item.MonthlyAveragePrice?.toFloatOrNull()
        val closingPrice = item.ClosingPrice?.toFloatOrNull()
        val change = item.Change?.toFloatOrNull()
        var cpColor = MaterialTheme.colorScheme.onBackground
        var cColor = MaterialTheme.colorScheme.onBackground
        // check color
        if (closingPrice != null && monthlyAveragePrice != null) {
            if (closingPrice > monthlyAveragePrice) {
                cpColor = Color.Red
            } else if (closingPrice < monthlyAveragePrice) {
                cpColor = Color.Green
            }
        }
        // check color
        if (change != null) {
            if (change > 0) {
                cColor = Color.Red
            } else if (change < 0) {
                cColor = Color.Green
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = item.Code, fontSize = 13.sp )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = item.Name.toString(), fontSize = 25.sp )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 30.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = "開盤價", fontSize = 15.sp)
            Text(text = item.OpeningPrice.toString(), fontSize = 20.sp )
            Text(text = "收盤價", fontSize = 15.sp)
            Text(text = item.ClosingPrice.toString(), fontSize = 20.sp, color = cpColor )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 30.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = "最高價", fontSize = 15.sp)
            Text(text = item.HighestPrice.toString(), fontSize = 20.sp )
            Text(text = "最低價", fontSize = 15.sp)
            Text(text = item.LowestPrice.toString(), fontSize = 20.sp )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 30.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = "漲跌價差", fontSize = 15.sp)
            Text(text = item.Change.toString(), fontSize = 20.sp,color = cColor )
            Text(text = "月平均價", fontSize = 15.sp)
            Text(text = item.MonthlyAveragePrice.toString(), fontSize = 20.sp)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Row {
                Text(text = "成交筆數", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = item.Transaction.toString(), fontSize = 14.sp)
            }
            Row {
                Text(text = "成交股數", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = item.TradeVolume.toString(), fontSize = 14.sp)
            }
            Row {
                Text(text = "成交金額", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = item.TradeValue.toString(), fontSize = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    StockReportTheme {
        MainScreen(MainViewModel(savedStateHandle = androidx.lifecycle.SavedStateHandle()))
    }
}

@Preview(showBackground = true)
@Composable
fun CardContentPreview() {
    StockReportTheme {
        CardContent(StockData("9105", "泰金寶-DR", "8.42", "8.40", "8.55", "8.35", "0.0500", "8.90", "10334", "54632322", "460971943", "31.29", "3.56", "2.06"))
    }
}