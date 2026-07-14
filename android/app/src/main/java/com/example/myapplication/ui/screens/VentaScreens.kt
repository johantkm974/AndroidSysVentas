package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.viewmodel.VentaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaListScreen(viewModel: VentaViewModel, navController: NavController) {
    val sales by viewModel.sales.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSales()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ventas Realizadas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(sales) { sale ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Venta: ${sale.numeroVenta}", style = MaterialTheme.typography.titleMedium)
                            Text(sale.estado, color = if (sale.estado == "ANULADO") Color.Red else Color.Green)
                        }
                        Text("Pedido Ref: ${sale.numeroPedido}")
                        Text("Cliente: ${sale.cliente}")
                        Text("Total: S/ ${sale.total}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Text("Método Pago: ${sale.metodoPago}")
                        
                        if (sale.estado != "ANULADO") {
                            TextButton(
                                onClick = { viewModel.annulSale(sale.idVenta) },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                            ) {
                                Text("Anular Venta")
                            }
                        }
                    }
                }
            }
        }
    }
}
