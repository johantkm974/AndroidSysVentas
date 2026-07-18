package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.EnvioResponse
import com.example.myapplication.model.SeguimientoResponse
import com.example.myapplication.ui.viewmodel.DeliveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryListScreen(viewModel: DeliveryViewModel, navController: NavController) {
    val deliveries by viewModel.deliveries.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadMyDeliveries() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Envíos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE65100),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (deliveries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes envíos asignados", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(deliveries) { envio ->
                    DeliveryCard(
                        envio = envio,
                        onViewDetail = { navController.navigate("delivery_detail/${envio.idEnvio}") }
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryCard(envio: EnvioResponse, onViewDetail: () -> Unit) {
    val estado = envio.estadoEnvio?.nombre ?: "DESCONOCIDO"
    val statusColor = when (estado) {
        "PENDIENTE" -> Color(0xFFFFA000)
        "EN_RUTA" -> Color(0xFF1976D2)
        "ENTREGADO" -> Color(0xFF388E3C)
        "CANCELADO" -> Color(0xFFD32F2F)
        else -> Color.Gray
    }

    Card(
        onClick = onViewDetail,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Envío #${envio.idEnvio}", fontWeight = FontWeight.SemiBold)
                Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.15f)) {
                    Text(
                        estado,
                        color = statusColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Dirección: ${envio.direccion}", style = MaterialTheme.typography.bodyMedium)
            Text("Distrito: ${envio.distrito}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailScreen(
    envioId: Long,
    viewModel: DeliveryViewModel,
    navController: NavController
) {
    val envio by viewModel.selectedEnvio.collectAsState()
    val tracking by viewModel.tracking.collectAsState()

    LaunchedEffect(envioId) {
        viewModel.loadEnvio(envioId)
        viewModel.loadTracking(envioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Envío") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE65100),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            envio?.let { e ->
                val estadoActual = e.estadoEnvio?.nombre ?: ""

                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Dirección: ${e.direccion}", fontWeight = FontWeight.SemiBold)
                        Text("Distrito: ${e.distrito}")
                        if (e.referencia != null) Text("Referencia: ${e.referencia}")
                        Text("Estado: ${estadoActual}", color = when (estadoActual) {
                            "PENDIENTE" -> Color(0xFFFFA000)
                            "EN_RUTA" -> Color(0xFF1976D2)
                            "ENTREGADO" -> Color(0xFF388E3C)
                            else -> Color.Gray
                        })
                    }
                }

                if (estadoActual == "PENDIENTE") {
                    Button(
                        onClick = { viewModel.updateToInRoute(envioId) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar Ruta")
                    }
                }

                if (estadoActual == "EN_RUTA") {
                    Button(
                        onClick = { viewModel.updateToDelivered(envioId) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Marcar como Entregado")
                    }
                }

                Text("Historial de Tracking", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (tracking.isEmpty()) {
                    Text("Sin seguimiento disponible", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    tracking.forEach { seg ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    seg.estadoEnvio?.nombre ?: "",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                if (seg.observacion != null) Text(seg.observacion, style = MaterialTheme.typography.bodySmall)
                                if (seg.createdAt != null) Text(seg.createdAt, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } ?: Text("Cargando...")
        }
    }
}
