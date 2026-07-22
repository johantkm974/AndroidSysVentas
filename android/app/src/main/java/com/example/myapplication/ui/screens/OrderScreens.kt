package com.example.myapplication.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.PedidoResponse
import com.example.myapplication.ui.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(viewModel: OrderViewModel, navController: NavController, isAdminOrSeller: Boolean = false, initialFilter: String? = null) {
    val orders by viewModel.orders.collectAsState()
    val repartidores by viewModel.repartidores.collectAsState()
    val assignError by viewModel.assignError.collectAsState()
    val assignSuccess by viewModel.assignSuccess.collectAsState()
    var showRepartidorDialog by remember { mutableStateOf(false) }
    var selectedPedidoId by remember { mutableLongStateOf(0L) }
    var selectedFilter by remember { mutableStateOf<String?>(initialFilter) }
    val snackbarHostState = remember { SnackbarHostState() }

    val estados = listOf("PENDIENTE", "CONFIRMADO", "PREPARANDO", "ENVIADO", "ENTREGADO", "CANCELADO")
    val filteredOrders = if (selectedFilter == null) orders
        else orders.filter { it.estado == selectedFilter }

    LaunchedEffect(Unit) {
        if (isAdminOrSeller) {
            viewModel.loadAllOrders()
            viewModel.loadRepartidores()
        } else {
            viewModel.loadMyOrders()
        }
    }

    LaunchedEffect(assignSuccess) {
        assignSuccess?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearAssignSuccess()
        }
    }

    LaunchedEffect(assignError) {
        assignError?.let {
            snackbarHostState.showSnackbar("Error: $it", duration = SnackbarDuration.Long)
            viewModel.clearAssignError()
        }
    }

    if (showRepartidorDialog && selectedPedidoId != 0L) {
        AlertDialog(
            onDismissRequest = {
                showRepartidorDialog = false
                viewModel.clearAssignError()
            },
            title = { Text("Asignar Repartidor") },
            text = {
                if (repartidores.isEmpty()) {
                    Text("No hay repartidores disponibles")
                } else {
                    LazyColumn {
                        items(repartidores) { repartidor ->
                            TextButton(
                                onClick = {
                                    viewModel.assignRepartidor(selectedPedidoId, repartidor.idUsuario)
                                    showRepartidorDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${repartidor.nombres} ${repartidor.apellidos}")
                            }
                        }
                    }
                }
                if (assignError != null) {
                    Text(assignError!!, color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showRepartidorDialog = false
                    viewModel.clearAssignError()
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isAdminOrSeller) "Gestión de Pedidos" else "Mis Pedidos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isAdminOrSeller) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("Todos (${orders.size})") }
                    )
                    estados.forEach { estado ->
                        val count = orders.count { it.estado == estado }
                        if (count > 0) {
                            FilterChip(
                                selected = selectedFilter == estado,
                                onClick = { selectedFilter = if (selectedFilter == estado) null else estado },
                                label = { Text("$estado ($count)") }
                            )
                        }
                    }
                }
            }

            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (orders.isEmpty()) "No hay pedidos disponibles"
                        else "No hay pedidos con este estado",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { order ->
                        OrderListItem(
                            order = order,
                            isAdminOrSeller = isAdminOrSeller,
                            onAssignRepartidor = {
                                selectedPedidoId = order.idPedido
                                showRepartidorDialog = true
                            },
                            onCancel = { viewModel.cancelOrder(order.idPedido) },
                            onClickDetail = {
                                navController.navigate("order_detail/${order.idPedido}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderListItem(
    order: PedidoResponse,
    isAdminOrSeller: Boolean,
    onAssignRepartidor: () -> Unit,
    onCancel: () -> Unit,
    onClickDetail: () -> Unit = {}
) {
    val statusColor = when (order.estado) {
        "PENDIENTE" -> Color(0xFFFFA000)
        "CONFIRMADO" -> Color(0xFF1976D2)
        "PREPARANDO" -> Color(0xFF7B1FA2)
        "ENVIADO" -> Color(0xFFFF6F00)
        "ENTREGADO" -> Color(0xFF388E3C)
        "CANCELADO" -> Color(0xFFD32F2F)
        else -> Color.Gray
    }

    val envioStatusColor = when (order.estadoEnvio) {
        "PENDIENTE" -> Color(0xFFFFA000)
        "EN_RUTA" -> Color(0xFF1976D2)
        "ENTREGADO" -> Color(0xFF388E3C)
        "CANCELADO" -> Color(0xFFD32F2F)
        else -> null
    }

    val hasEnvio = order.idEnvio != null
    val isClickable = !isAdminOrSeller && hasEnvio

    Card(
        onClick = { if (isClickable) onClickDetail() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Pedido: ${order.numeroPedido}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (order.estadoEnvio != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = envioStatusColor ?: Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Envío: ${formatearEstadoEnvio(order.estadoEnvio)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = envioStatusColor ?: Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            if (order.repartidor != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Repartidor: ${order.repartidor}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (order.estadoEnvio != null && order.estado != "CANCELADO" && order.estado != "ENTREGADO") {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Sin repartidor asignado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Cliente: ${order.cliente}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Total: S/ ${order.total}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            if (!order.detalles.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Detalles:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                order.detalles.forEach { detail ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "• ${detail.producto} x${detail.cantidad} - S/ ${detail.subtotal}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = statusColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                order.estado,
                                color = statusColor,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isAdminOrSeller && order.estado != "CANCELADO" && order.estado != "ENTREGADO") {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (order.estado == "CONFIRMADO" || order.estado == "PREPARANDO") {
                        FilledTonalButton(
                            onClick = onAssignRepartidor,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Asignar Repartidor")
                        }
                    }
                }
            } else if (!isAdminOrSeller && (order.estado == "PENDIENTE" || order.estado == "CONFIRMADO")) {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cancelar Pedido")
                }
            }
        }
    }
}

fun formatearEstadoEnvio(estado: String): String {
    return when (estado) {
        "PENDIENTE" -> "Pendiente"
        "EN_RUTA" -> "En camino"
        "ENTREGADO" -> "Entregado"
        "CANCELADO" -> "Cancelado"
        else -> estado
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    pedidoId: Long,
    viewModel: OrderViewModel,
    navController: NavController
) {
    val orders by viewModel.orders.collectAsState()
    val envio by viewModel.selectedEnvio.collectAsState()
    val tracking by viewModel.tracking.collectAsState()

    val order = orders.find { it.idPedido == pedidoId }

    LaunchedEffect(pedidoId) {
        viewModel.clearEnvioAndTracking()
        viewModel.loadEnvioByPedido(pedidoId)
    }

    LaunchedEffect(envio) {
        envio?.let { viewModel.loadTracking(it.idEnvio) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguimiento de Pedido") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearEnvioAndTracking()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            order?.let { o ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Pedido: ${o.numeroPedido}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        val statusColor = when (o.estado) {
                            "PENDIENTE" -> Color(0xFFFFA000)
                            "CONFIRMADO" -> Color(0xFF1976D2)
                            "PREPARANDO" -> Color(0xFF7B1FA2)
                            "ENVIADO" -> Color(0xFFFF6F00)
                            "ENTREGADO" -> Color(0xFF388E3C)
                            "CANCELADO" -> Color(0xFFD32F2F)
                            else -> Color.Gray
                        }
                        Text(
                            "Estado: ${o.estado}",
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Total: S/ ${o.total}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!o.detalles.isNullOrEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Detalles del Pedido",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            o.detalles.forEach { d ->
                                Text(
                                    "• ${d.producto} x${d.cantidad} - S/ ${d.subtotal}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            envio?.let { e ->
                val envioNombre = e.estadoEnvio?.nombre ?: ""
                val envioStatusColor = when (envioNombre) {
                    "PENDIENTE" -> Color(0xFFFFA000)
                    "EN_RUTA" -> Color(0xFF1976D2)
                    "ENTREGADO" -> Color(0xFF388E3C)
                    "CANCELADO" -> Color(0xFFD32F2F)
                    else -> Color.Gray
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Progreso del Envío",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val progress = when (envioNombre) {
                            "PENDIENTE" -> 0.33f
                            "EN_RUTA" -> 0.66f
                            "ENTREGADO" -> 1f
                            else -> 0f
                        }
                        val progressColor = when (envioNombre) {
                            "CANCELADO" -> Color(0xFFD32F2F)
                            else -> Color(0xFF1976D2)
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = progressColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Pendiente", "En camino", "Entregado").forEachIndexed { i, label ->
                                val step = (i + 1) * 0.33f
                                val isActive = progress >= step
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = when {
                                            isActive -> Icons.Default.CheckCircle
                                            envioNombre == "CANCELADO" -> Icons.Default.Cancel
                                            else -> Icons.Default.Schedule
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = if (isActive) Color(0xFF1976D2) else if (envioNombre == "CANCELADO") Color(0xFFD32F2F) else Color.Gray
                                    )
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isActive) Color(0xFF1976D2) else if (envioNombre == "CANCELADO" && !isActive) Color(0xFFD32F2F).copy(alpha = 0.5f) else Color.Gray,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Información del Envío",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Dirección: ${e.direccion}")
                        Text("Distrito: ${e.distrito}")
                        if (e.referencia != null) Text("Referencia: ${e.referencia}")
                        if (e.repartidor != null) Text("Repartidor: ${e.repartidor}")
                        Text(
                            "Estado: ${formatearEstadoEnvio(envioNombre)}",
                            color = envioStatusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    "Historial de Seguimiento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (tracking.isEmpty()) {
                    Text(
                        "Sin seguimiento disponible",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    tracking.forEach { seg ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    seg.estadoEnvio?.nombre?.let { formatearEstadoEnvio(it) } ?: "",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                if (seg.observacion != null) {
                                    Text(
                                        seg.observacion,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                if (seg.createdAt != null) {
                                    Text(
                                        seg.createdAt,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (order == null && envio == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
