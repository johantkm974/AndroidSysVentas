package com.example.myapplication.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.Categoria
import com.example.myapplication.model.Marca
import com.example.myapplication.model.ProductoRequest
import com.example.myapplication.model.ProductoResponse
import com.example.myapplication.model.Proveedor
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.viewmodel.ProductUiState
import com.example.myapplication.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryManagementScreen(
    viewModel: ProductViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
        viewModel.loadCategories()
    }
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Inventario") },
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
                onClick = { navController.navigate(Screen.AddProduct.route) },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
            }
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
                    selected = selectedCategoryId == null,
                    onClick = { viewModel.loadProducts() },
                    label = { Text("Todas") }
                )
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategoryId == cat.idCategoria,
                        onClick = { viewModel.loadProductsByCategory(cat.idCategoria) },
                        label = { Text(cat.nombre) }
                    )
                }
            }
            when (uiState) {
                is ProductUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ProductUiState.Success -> {
                    val products = (uiState as ProductUiState.Success).products
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(products) { product ->
                            InventoryProductItem(
                                product = product,
                                onDelete = { viewModel.deleteProduct(product.idProducto) },
                                onEdit = { navController.navigate(Screen.EditProduct.createRoute(product.idProducto)) }
                            )
                        }
                    }
                }
                is ProductUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (uiState as ProductUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryProductItem(product: ProductoResponse, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Código: ${product.codigo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row {
                    Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall,
                        color = if (product.stock <= 5) MaterialTheme.colorScheme.error
                               else MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("S/ ${product.precioVenta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
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
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) }
    var selectedMarca by remember { mutableStateOf<Marca?>(null) }
    var selectedProveedor by remember { mutableStateOf<Proveedor?>(null) }
    var categoriaExpanded by remember { mutableStateOf(false) }
    var marcaExpanded by remember { mutableStateOf(false) }
    var proveedorExpanded by remember { mutableStateOf(false) }

    val categories by viewModel.categories.collectAsState()
    val marcas by viewModel.marcas.collectAsState()
    val proveedores by viewModel.proveedores.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.loadMarcas()
        viewModel.loadProveedores()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Producto") },
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = codigo, onValueChange = { codigo = it }, label = { Text("Código") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = precioCompra, onValueChange = { precioCompra = it }, label = { Text("Precio Compra") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = precioVenta, onValueChange = { precioVenta = it }, label = { Text("Precio Venta") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = stockMinimo, onValueChange = { stockMinimo = it }, label = { Text("Stock Mínimo") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL Imagen") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

            ExposedDropdownMenuBox(
                expanded = categoriaExpanded,
                onExpandedChange = { categoriaExpanded = !categoriaExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategoria?.nombre ?: "Seleccionar categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = categoriaExpanded, onDismissRequest = { categoriaExpanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = { selectedCategoria = cat; categoriaExpanded = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = marcaExpanded,
                onExpandedChange = { marcaExpanded = !marcaExpanded }
            ) {
                OutlinedTextField(
                    value = selectedMarca?.nombre ?: "Seleccionar marca",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Marca") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = marcaExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = marcaExpanded, onDismissRequest = { marcaExpanded = false }) {
                    marcas.forEach { marca ->
                        DropdownMenuItem(
                            text = { Text(marca.nombre) },
                            onClick = { selectedMarca = marca; marcaExpanded = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = proveedorExpanded,
                onExpandedChange = { proveedorExpanded = !proveedorExpanded }
            ) {
                OutlinedTextField(
                    value = selectedProveedor?.razonSocial ?: "Seleccionar proveedor",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Proveedor") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = proveedorExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = proveedorExpanded, onDismissRequest = { proveedorExpanded = false }) {
                    proveedores.forEach { prov ->
                        DropdownMenuItem(
                            text = { Text(prov.razonSocial) },
                            onClick = { selectedProveedor = prov; proveedorExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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
                        idCategoria = selectedCategoria?.idCategoria ?: 1,
                        idMarca = selectedMarca?.idMarca ?: 1,
                        idProveedor = selectedProveedor?.idProveedor ?: 1
                    )
                    viewModel.createProduct(request)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Producto", style = MaterialTheme.typography.labelLarge)
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
    var imagenUrl by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) }
    var selectedMarca by remember { mutableStateOf<Marca?>(null) }
    var selectedProveedor by remember { mutableStateOf<Proveedor?>(null) }
    var categoriaExpanded by remember { mutableStateOf(false) }
    var marcaExpanded by remember { mutableStateOf(false) }
    var proveedorExpanded by remember { mutableStateOf(false) }

    val categories by viewModel.categories.collectAsState()
    val marcas by viewModel.marcas.collectAsState()
    val proveedores by viewModel.proveedores.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.loadMarcas()
        viewModel.loadProveedores()
    }

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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = codigo, onValueChange = { codigo = it }, label = { Text("Código") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = precioCompra, onValueChange = { precioCompra = it }, label = { Text("Precio Compra") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = precioVenta, onValueChange = { precioVenta = it }, label = { Text("Precio Venta") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = stockMinimo, onValueChange = { stockMinimo = it }, label = { Text("Stock Mínimo") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL Imagen") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

            ExposedDropdownMenuBox(
                expanded = categoriaExpanded,
                onExpandedChange = { categoriaExpanded = !categoriaExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategoria?.nombre ?: "Seleccionar categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = categoriaExpanded, onDismissRequest = { categoriaExpanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = { selectedCategoria = cat; categoriaExpanded = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = marcaExpanded,
                onExpandedChange = { marcaExpanded = !marcaExpanded }
            ) {
                OutlinedTextField(
                    value = selectedMarca?.nombre ?: "Seleccionar marca",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Marca") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = marcaExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = marcaExpanded, onDismissRequest = { marcaExpanded = false }) {
                    marcas.forEach { marca ->
                        DropdownMenuItem(
                            text = { Text(marca.nombre) },
                            onClick = { selectedMarca = marca; marcaExpanded = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = proveedorExpanded,
                onExpandedChange = { proveedorExpanded = !proveedorExpanded }
            ) {
                OutlinedTextField(
                    value = selectedProveedor?.razonSocial ?: "Seleccionar proveedor",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Proveedor") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = proveedorExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = proveedorExpanded, onDismissRequest = { proveedorExpanded = false }) {
                    proveedores.forEach { prov ->
                        DropdownMenuItem(
                            text = { Text(prov.razonSocial) },
                            onClick = { selectedProveedor = prov; proveedorExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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
                        idCategoria = selectedCategoria?.idCategoria ?: 1,
                        idMarca = selectedMarca?.idMarca ?: 1,
                        idProveedor = selectedProveedor?.idProveedor ?: 1
                    )
                    viewModel.updateProduct(id, request)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Actualizar Producto", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
