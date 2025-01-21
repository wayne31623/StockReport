package com.example.stockreport.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockreport.model.AssetsFileUtil
import com.example.stockreport.model.StockData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val savedStateHandle: SavedStateHandle): ViewModel() {
    private val _uiState = MutableStateFlow<List<StockData>>(emptyList())
    val uiState: StateFlow<List<StockData>> = _uiState.asStateFlow()

    init {
        val savedUiState = savedStateHandle.get<List<StockData>>("uiState")
        if(savedUiState != null) {
            _uiState.value = savedUiState
        } else {
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val dataList = AssetsFileUtil.getUiStateData()
            _uiState.value = dataList
        }
    }

    fun reverseList() {
        _uiState.value = _uiState.value.reversed()
        savedStateHandle.set("uiState", _uiState.value)
    }
}