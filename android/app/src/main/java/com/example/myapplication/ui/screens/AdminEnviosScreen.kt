package com.example.myapplication.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.myapplication.ui.viewmodel.DeliveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEnviosScreen(viewModel: DeliveryViewModel, navController: NavController) {
    val allEnvios by viewModel.allEnvios.collectAsState()
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    val estados = listOf("PENDIENTE", "EN_RUTA", "ENTREGADO", "CANCELADO")
    val filteredEnvios = if (selectedFilter == null) allEnvios
        else allEnvios.filter { it.estadoEnvio?.nombre == selectedFilter }

    LaunchedEffect(Unit) { viewModel.loadAllEnvios() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Envíos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC62828),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
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
                    label = { Text("Todos (${allEnvios.size})") }
                )
                estados.forEach { estado ->
                    val count = allEnvios.count { it.estadoEnvio?.nombre == estado }
                    FilterChip(
                        selected = selectedFilter == estado,
                        onClick = { selectedFilter = if (selectedFilter == estado) null else estado },
                        label = { Text("$estado ($count)") }
                    )
                }
            }

            if (filteredEnvios.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (allEnvios.isEmpty()) "No hay envíos registrados"
                        else "No hay envíos con este estado",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredEnvios) { envio ->
                        AdminEnvioCard(
                            envio = envio,
                            onClick = { navController.navigate("delivery_detail/${envio.idEnvio}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminEnvioCard(envio: EnvioResponse, onClick: () -> Unit) {
    val estado = envio.estadoEnvio?.nombre ?: "DESCONOCIDO"
    val statusColor = when (estado) {
        "PENDIENTE" -> Color(0xFFFFA000)
        "EN_RUTA" -> Color(0xFF1976D2)
        "ENTREGADO" -> Color(0xFF388E3C)
        "CANCELADO" -> Color(0xFFD32F2F)
        else -> Color.Gray
    }

    Card(
        onClick = onClick,
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
            if (envio.repartidor != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Repartidor: ${envio.repartidor}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
