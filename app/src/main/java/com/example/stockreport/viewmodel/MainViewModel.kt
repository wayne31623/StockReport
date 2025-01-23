package com.example.stockreport.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockreport.model.data.StockData
import com.example.stockreport.model.StockDataRepository
import com.example.stockreport.network.TWSEApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val savedStateHandle: SavedStateHandle): ViewModel() {
    private val _uiState = MutableStateFlow<List<StockData>>(emptyList())
    val uiState: StateFlow<List<StockData>> = _uiState.asStateFlow()

    init {
        // check saved state
        val savedUiState = savedStateHandle.get<List<StockData>>("uiState")
        if(savedUiState != null) {
            _uiState.value = savedUiState
        } else {
            // load data
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            // call api
            val dataList = StockDataRepository(TWSEApi.apiService).fetchAndMergeData()
            // save data
            _uiState.value = dataList
        }
    }

    fun reverseList() {
        // reverse list
        _uiState.value = _uiState.value.reversed()
        // save data
        savedStateHandle.set("uiState", _uiState.value)
    }
}