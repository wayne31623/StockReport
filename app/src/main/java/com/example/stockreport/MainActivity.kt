package com.example.stockreport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockreport.model.StockData
import com.example.stockreport.ui.theme.GhostWhite
import com.example.stockreport.ui.theme.StockReportTheme
import com.example.stockreport.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: MainViewModel by viewModels()
        super.onCreate(savedInstanceState)
        setContent {
            StockReportTheme {
                val uiState by viewModel.uiState.collectAsState()
                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()
                var showBottomSheet by remember { mutableStateOf(false) }
                var isDescending by remember { mutableStateOf(true) }
                val showDialog = remember { mutableStateOf(false) }
                val darkTheme = isSystemInDarkTheme()
                var cContainerColor = MaterialTheme.colorScheme.surfaceVariant
                var bsContainerColor = MaterialTheme.colorScheme.surface
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            CustomImageButton(
                                draw = R.drawable.filter,
                                onClick = {
                                    showBottomSheet = true
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (uiState.isEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(64.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        }
                        else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(uiState.size) { index ->
                                    val item = uiState[index]
                                    Card(
                                        onClick = {
                                            showDialog.value = true
                                        },
                                        colors = CardDefaults.cardColors(
                                            containerColor = cContainerColor
                                        ),
                                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
                                    ) {
                                        if (showDialog.value) {
                                            AlertDialog(
                                                onDismissRequest = { showDialog.value = false },
                                                title = {
                                                    Text(text = "資訊顯示")
                                                },
                                                text = {
                                                    Text(text = "本益比 ${item.PEratio}\n殖利率 ${item.DividendYield}%\n股價淨值比 ${item.PBratio}")
                                                },
                                                confirmButton = {
                                                    Button(onClick = { showDialog.value = false }) {
                                                        Text("確定")
                                                    }
                                                }
                                            )
                                        }
                                        CardContent(item)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }

                        }
                    }
                    if (showBottomSheet) {
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
        }
    }
}

@Composable
fun CardContent(item: StockData) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val monthlyAveragePrice = item.MonthlyAveragePrice?.toFloatOrNull()
        val closingPrice = item.ClosingPrice?.toFloatOrNull()
        val change = item.Change?.toFloatOrNull()
        var cpColor = MaterialTheme.colorScheme.onBackground
        var cColor = MaterialTheme.colorScheme.onBackground
        if (closingPrice != null && monthlyAveragePrice != null) {
            if (closingPrice > monthlyAveragePrice) {
                cpColor = Color.Red
            } else if (closingPrice < monthlyAveragePrice) {
                cpColor = Color.Green
            }
        }
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
                Text(text = "成交筆數", fontSize = 15.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = item.Transaction.toString(), fontSize = 15.sp)
            }
            Row {
                Text(text = "成交股數", fontSize = 15.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = item.TradeVolume.toString(), fontSize = 15.sp)
            }
            Row {
                Text(text = "成交金額", fontSize = 15.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = item.TradeValue.toString(), fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun CustomImageButton(draw: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .size(48.dp)
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = draw),
            contentDescription = ""
        )
    }
}