package com.jorge.jorgeoviedo_comp304_test2.funs

import android.content.Context
import org.xmlpull.v1.XmlPullParser
import androidx.compose.runtime.LaunchedEffect
import com.jorge.jorgeoviedo_comp304_test2.R
import com.jorge.jorgeoviedo_comp304_test2.roomDB.Stock
import com.jorge.jorgeoviedo_comp304_test2.roomDB.StockDao
import com.jorge.jorgeoviedo_comp304_test2.roomDB.StockDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParserFactory


fun parseStocksXml(context: Context): List<Stock> {
    val stockList = mutableListOf<Stock>()
    val inputStream = context.resources.openRawResource(R.raw.stocks)
    val parser = XmlPullParserFactory.newInstance().newPullParser()
    parser.setInput(inputStream, null)

    var eventType = parser.eventType
    var stockSymbol = ""
    var companyName = ""
    var value = 0.0

    while (eventType != XmlPullParser.END_DOCUMENT) {
        val name = parser.name
        when (eventType) {
            XmlPullParser.START_TAG -> {
                when (name) {
                    "stockSymbol" -> stockSymbol = parser.nextText()
                    "companyName" -> companyName = parser.nextText()
                    "value" -> value = parser.nextText().toDouble()
                }
            }
            XmlPullParser.END_TAG -> {
                if (name == "stock") {
                    stockList.add(Stock(stockSymbol, companyName, value))
                }
            }
        }
        eventType = parser.next()
    }
    return stockList
}

suspend fun parseAndInsertStocks(context: Context, stockDao: StockDao) {
    val stocks = withContext(Dispatchers.IO) { parseStocksXml(context) }
    val existingSymbols = withContext(Dispatchers.IO) { stockDao.getAllStocks().map { it.stockSymbol } }
    val uniqueStocks = stocks.filter { it.stockSymbol !in existingSymbols }

    withContext(Dispatchers.IO) {
        stockDao.insertAll(*uniqueStocks.toTypedArray())
    }
}
