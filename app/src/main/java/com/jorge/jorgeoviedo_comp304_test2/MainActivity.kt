package com.jorge.jorgeoviedo_comp304_test2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jorge.jorgeoviedo_comp304_test2.ui.theme.JorgeOviedo_COMP304_Test2Theme

import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jorge.jorgeoviedo_comp304_test2.composables.StockDetailsScreen
import com.jorge.jorgeoviedo_comp304_test2.funs.parseAndInsertStocks
import com.jorge.jorgeoviedo_comp304_test2.roomDB.Stock
import com.jorge.jorgeoviedo_comp304_test2.roomDB.StockDatabase
import com.jorge.jorgeoviedo_comp304_test2.viewModels.StockViewModel
import com.jorge.jorgeoviedo_comp304_test2.viewModels.StockViewModelFactory
import com.jorge.jorgeoviedo_comp304_test2.worker.SaveDatabaseWorker
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var workManager: WorkManager
    private lateinit var stockViewModel: StockViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(applicationContext)

        val stockDao = StockDatabase.getDatabase(this).stockDao()
        val viewModelFactory = StockViewModelFactory(stockDao, this)
        stockViewModel = ViewModelProvider(this, viewModelFactory)[StockViewModel::class.java]

        setContent {
            MyApp(stockViewModel)
        }
    }

    override fun onPause() {
        super.onPause()
        val request = OneTimeWorkRequestBuilder<SaveDatabaseWorker>().build()
        workManager.enqueue(request)
    }
}

@Composable
fun MyApp(viewModel: StockViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val stocks by viewModel.stocks.observeAsState(listOf())
    var selectedStock by remember { mutableStateOf<Stock?>(null) }
    var buttonEnabled by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }

    var stockSymbol by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var stockValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchStocks()
    }

    if (showDetails && selectedStock != null) {
        StockDetailsScreen(stock = selectedStock!!, onBack = { showDetails = false })
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = stockSymbol,
                onValueChange = { stockSymbol = it },
                label = { Text("Stock Symbol") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = stockValue,
                onValueChange = { stockValue = it },
                label = { Text("Stock Value") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val value = stockValue.toDoubleOrNull()
                    if (stockSymbol.isNotEmpty() && companyName.isNotEmpty() && value != null) {
                        viewModel.addStock(Stock(stockSymbol, companyName, value))
                        stockSymbol = ""
                        companyName = ""
                        stockValue = ""
                    } else {
                        Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Database")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.clearDatabase()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Database")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(stocks) { stock ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${stock.stockSymbol}: ${stock.value}")
                        RadioButton(
                            selected = selectedStock == stock,
                            onClick = {
                                selectedStock = stock
                                buttonEnabled = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    showDetails = true
                },
                enabled = buttonEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Stock Info")
            }
        }
    }
}





