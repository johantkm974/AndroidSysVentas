package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.*
import com.example.myapplication.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaManagementScreen(viewModel: CategoriaViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val categorias by viewModel.categorias.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadCategorias() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestión de Categorías") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Categoría")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(categorias) { cat ->
                ListItem(
                    headlineContent = { Text(cat.nombre) },
                    supportingContent = { Text(cat.descripcion ?: "Sin descripción") }
                )
            }
        }

        if (showDialog) {
            var nombre by remember { mutableStateOf("") }
            var desc by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Nueva Categoría") },
                text = {
                    Column {
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.createCategoria(Categoria(nombre = nombre, descripcion = desc))
                        showDialog = false
                    }) { Text("Guardar") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcaManagementScreen(viewModel: MarcaViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val marcas by viewModel.marcas.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadMarcas() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestión de Marcas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Marca")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(marcas) { marca ->
                ListItem(headlineContent = { Text(marca.nombre) })
            }
        }

        if (showDialog) {
            var nombre by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Nueva Marca") },
                text = {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.createMarca(Marca(nombre = nombre))
                        showDialog = false
                    }) { Text("Guardar") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedorManagementScreen(viewModel: ProveedorViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val proveedores by viewModel.proveedores.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadProveedores() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestión de Proveedores") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Proveedor")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(proveedores) { prov ->
                ListItem(
                    headlineContent = { Text(prov.razonSocial) },
                    supportingContent = { Text("RUC: ${prov.ruc}") }
                )
            }
        }

        if (showDialog) {
            var nombre by remember { mutableStateOf("") }
            var ruc by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Nuevo Proveedor") },
                text = {
                    Column {
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Razón Social") })
                        OutlinedTextField(value = ruc, onValueChange = { ruc = it }, label = { Text("RUC") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.createProveedor(Proveedor(razonSocial = nombre, ruc = ruc))
                        showDialog = false
                    }) { Text("Guardar") }
                }
            )
        }
    }
}
