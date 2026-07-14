package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.*
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.viewmodel.ProductUiState
import com.example.myapplication.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryManagementScreen(
    viewModel: ProductViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Inventario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddProduct.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (uiState) {
                is ProductUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is ProductUiState.Success -> {
                    val products = (uiState as ProductUiState.Success).products
                    LazyColumn {
                        items(products) { product ->
                            InventoryProductItem(
                                product = product,
                                onDelete = { viewModel.deleteProduct(product.idProducto) },
                                onEdit = { navController.navigate(Screen.EditProduct.createRoute(product.idProducto)) }
                            )
                        }
                    }
                }
                is ProductUiState.Error -> Text((uiState as ProductUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun InventoryProductItem(product: ProductoResponse, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.nombre, style = MaterialTheme.typography.titleMedium)
                Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
                Text("Precio: S/ ${product.precioVenta}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: ProductViewModel,
    navController: NavController
) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var precioCompra by remember { mutableStateOf("") }
    var precioVenta by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(value = codigo, onValueChange = { codigo = it }, label = { Text("Código") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            OutlinedTextField(value = precioCompra, onValueChange = { precioCompra = it }, label = { Text("Precio Compra") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = precioVenta, onValueChange = { precioVenta = it }, label = { Text("Precio Venta") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stockMinimo, onValueChange = { stockMinimo = it }, label = { Text("Stock Mínimo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL Imagen") }, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    val request = ProductoRequest(
                        codigo = codigo,
                        nombre = nombre,
                        descripcion = descripcion,
                        precioCompra = precioCompra.toDoubleOrNull() ?: 0.0,
                        precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        stockMinimo = stockMinimo.toIntOrNull() ?: 0,
                        imagen = if (imagenUrl.isBlank()) null else imagenUrl,
                        idCategoria = 1,
                        idMarca = 1,
                        idProveedor = 1
                    )
                    viewModel.createProduct(request)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Producto")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    id: Long,
    viewModel: ProductViewModel,
    navController: NavController
) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var precioCompra by remember { mutableStateOf("") }
    var precioVenta by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var idCategoria by remember { mutableLongStateOf(1L) }
    var idMarca by remember { mutableLongStateOf(1L) }
    var idProveedor by remember { mutableLongStateOf(1L) }
    var imagenUrl by remember { mutableStateOf("") }

    LaunchedEffect(id) {
        viewModel.getProductById(id)?.let { product ->
            codigo = product.codigo
            nombre = product.nombre
            precioCompra = product.precioCompra.toString()
            precioVenta = product.precioVenta.toString()
            stock = product.stock.toString()
            stockMinimo = product.stockMinimo.toString()
            descripcion = product.descripcion ?: ""
            imagenUrl = product.imagen ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto #$id") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(value = codigo, onValueChange = { codigo = it }, label = { Text("Código") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            OutlinedTextField(value = precioCompra, onValueChange = { precioCompra = it }, label = { Text("Precio Compra") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = precioVenta, onValueChange = { precioVenta = it }, label = { Text("Precio Venta") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stockMinimo, onValueChange = { stockMinimo = it }, label = { Text("Stock Mínimo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL Imagen") }, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    val request = ProductoRequest(
                        codigo = codigo,
                        nombre = nombre,
                        descripcion = descripcion,
                        precioCompra = precioCompra.toDoubleOrNull() ?: 0.0,
                        precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        stockMinimo = stockMinimo.toIntOrNull() ?: 0,
                        imagen = if (imagenUrl.isBlank()) null else imagenUrl,
                        idCategoria = idCategoria,
                        idMarca = idMarca,
                        idProveedor = idProveedor
                    )
                    viewModel.updateProduct(id, request)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar Producto")
            }
        }
    }
}
