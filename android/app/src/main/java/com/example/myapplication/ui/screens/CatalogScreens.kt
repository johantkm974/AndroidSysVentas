package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.*
import com.example.myapplication.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaManagementScreen(viewModel: CategoriaViewModel, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val categorias by viewModel.categorias.collectAsState()
    var dialogError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) { viewModel.loadCategorias() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Categorías") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Categoría")
            }
        }
    ) { padding ->
        if (categorias.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay categorías", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { cat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cat.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text(cat.descripcion ?: "Sin descripción", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            cat.idCategoria?.let { id ->
                                IconButton(onClick = { viewModel.deleteCategoria(id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            var nombre by remember { mutableStateOf("") }
            var desc by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showDialog = false; dialogError = null },
                title = { Text("Nueva Categoría", fontWeight = FontWeight.SemiBold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it; dialogError = null }, label = { Text("Nombre *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
                        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done))
                        if (dialogError != null) {
                            Text(dialogError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nombre.isBlank()) {
                                dialogError = "Complete todos los campos"
                            } else {
                                viewModel.createCategoria(Categoria(nombre = nombre, descripcion = desc))
                                showDialog = false
                                dialogError = null
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false; dialogError = null }) { Text("Cancelar") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcaManagementScreen(viewModel: MarcaViewModel, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val marcas by viewModel.marcas.collectAsState()
    var dialogError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadMarcas() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Marcas") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Marca")
            }
        }
    ) { padding ->
        if (marcas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay marcas", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(marcas) { marca ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(marca.nombre, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            marca.idMarca?.let { id ->
                                IconButton(onClick = { viewModel.deleteMarca(id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            var nombre by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showDialog = false; dialogError = null },
                title = { Text("Nueva Marca", fontWeight = FontWeight.SemiBold) },
                text = {
                    Column {
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it; dialogError = null }, label = { Text("Nombre *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done))
                        if (dialogError != null) {
                            Text(dialogError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nombre.isBlank()) {
                                dialogError = "Complete todos los campos"
                            } else {
                                viewModel.createMarca(Marca(nombre = nombre))
                                showDialog = false
                                dialogError = null
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false; dialogError = null }) { Text("Cancelar") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedorManagementScreen(viewModel: ProveedorViewModel, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val proveedores by viewModel.proveedores.collectAsState()
    var dialogError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) { viewModel.loadProveedores() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Proveedores") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Proveedor")
            }
        }
    ) { padding ->
        if (proveedores.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay proveedores", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(proveedores) { prov ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(prov.razonSocial, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text("RUC: ${prov.ruc}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            prov.idProveedor?.let { id ->
                                IconButton(onClick = { viewModel.deleteProveedor(id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            var nombre by remember { mutableStateOf("") }
            var ruc by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showDialog = false; dialogError = null },
                title = { Text("Nuevo Proveedor", fontWeight = FontWeight.SemiBold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it; dialogError = null }, label = { Text("Razón Social *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
                        OutlinedTextField(value = ruc, onValueChange = { ruc = it; dialogError = null }, label = { Text("RUC *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done))
                        if (dialogError != null) {
                            Text(dialogError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val faltantes = mutableListOf<String>()
                            if (nombre.isBlank()) faltantes.add("Razón Social")
                            if (ruc.isBlank()) faltantes.add("RUC")
                            if (faltantes.isNotEmpty()) {
                                dialogError = "Complete todos los campos: ${faltantes.joinToString(", ")}"
                            } else {
                                viewModel.createProveedor(Proveedor(razonSocial = nombre, ruc = ruc))
                                showDialog = false
                                dialogError = null
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false; dialogError = null }) { Text("Cancelar") }
                }
            )
        }
    }
}
