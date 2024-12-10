package com.jorge.jorgeoviedo_comp304_test2.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_info")
data class Stock(
    @PrimaryKey val stockSymbol: String,
    val companyName: String,
    val value: Double
)
