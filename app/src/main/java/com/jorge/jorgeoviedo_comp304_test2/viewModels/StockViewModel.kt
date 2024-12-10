package com.jorge.jorgeoviedo_comp304_test2.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.content.Context
import com.jorge.jorgeoviedo_comp304_test2.funs.parseAndInsertStocks
import com.jorge.jorgeoviedo_comp304_test2.roomDB.Stock
import com.jorge.jorgeoviedo_comp304_test2.roomDB.StockDao

class StockViewModel(private val stockDao: StockDao) : ViewModel() {

    private val _stocks = MutableLiveData<List<Stock>>()
    val stocks: LiveData<List<Stock>> get() = _stocks

    fun fetchStocks() {
        viewModelScope.launch {
            _stocks.value = stockDao.getAllStocks()
        }
    }

    fun addStock(stock: Stock) {
        viewModelScope.launch {
            stockDao.insert(stock)
            fetchStocks()
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            stockDao.clearAll()
            fetchStocks()
        }
    }
}

