package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.PedidoResponse
import com.example.myapplication.ui.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(viewModel: OrderViewModel, navController: NavController, isAdminOrSeller: Boolean = false) {
    val orders by viewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        if (isAdminOrSeller) viewModel.loadAllOrders() else viewModel.loadMyOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isAdminOrSeller) "Gestión de Pedidos" else "Mis Pedidos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(orders) { order ->
                OrderListItem(
                    order = order,
                    isAdminOrSeller = isAdminOrSeller,
                    onStatusChange = { newStatus -> viewModel.updateStatus(order.idPedido, newStatus, isAdminOrSeller) },
                    onCancel = { viewModel.cancelOrder(order.idPedido) }
                )
            }
        }
    }
}

@Composable
fun OrderListItem(
    order: PedidoResponse,
    isAdminOrSeller: Boolean,
    onStatusChange: (Long) -> Unit,
    onCancel: () -> Unit
) {
    val statusColor = when (order.estado) {
        "PENDIENTE" -> Color(0xFFFFC107)
        "CONFIRMADO" -> Color(0xFF2196F3)
        "ENTREGADO" -> Color(0xFF4CAF50)
        "CANCELADO" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pedido: ${order.numeroPedido}", style = MaterialTheme.typography.titleMedium)
                Text(order.estado, color = statusColor, style = MaterialTheme.typography.labelLarge)
            }
            Text("Cliente: ${order.cliente}", style = MaterialTheme.typography.bodySmall)
            Text("Total: S/ ${order.total}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            
            if (order.detalles != null) {
                Spacer(modifier = Modifier.height(8.dp))
                order.detalles.forEach { detail ->
                    Text("• ${detail.producto} x${detail.cantidad}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isAdminOrSeller && order.estado != "CANCELADO" && order.estado != "ENTREGADO") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onStatusChange(2) }) { Text("Confirmar") }
                    TextButton(onClick = { onStatusChange(5) }) { Text("Entregar") }
                }
            } else if (!isAdminOrSeller && order.estado == "PENDIENTE") {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancelar Pedido")
                }
            }
        }
    }
}
