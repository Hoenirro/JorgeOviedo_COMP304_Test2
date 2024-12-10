package com.jorge.jorgeoviedo_comp304_test2.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface StockDao {
    @Insert
    suspend fun insertAll(vararg stocks: Stock)

    @Insert
    suspend fun insert(stock: Stock)

    @Update
    suspend fun update(stock: Stock)

    @Delete
    suspend fun delete(stock: Stock)

    @Query("DELETE FROM stock_info")
    suspend fun clearAll()

    @Query("SELECT * FROM stock_info")
    suspend fun getAllStocks(): List<Stock>

    @Query("SELECT * FROM stock_info WHERE stockSymbol = :symbol")
    suspend fun getStockBySymbol(symbol: String): Stock?
}


