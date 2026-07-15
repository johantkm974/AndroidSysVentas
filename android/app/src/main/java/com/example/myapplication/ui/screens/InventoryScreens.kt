package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
                    if (!product.imagen.isNullOrBlank()) {
                        AsyncImage(
                            model = product.imagen,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
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
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories by viewModel.categories.collectAsState()
    val marcas by viewModel.marcas.collectAsState()
    val proveedores by viewModel.proveedores.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.loadMarcas()
        viewModel.loadProveedores()
    }

    fun validate(): Boolean {
        val camposFaltantes = mutableListOf<String>()
        if (codigo.isBlank()) camposFaltantes.add("Código")
        if (nombre.isBlank()) camposFaltantes.add("Nombre")
        if (precioCompra.isBlank()) camposFaltantes.add("Precio Compra")
        if (precioVenta.isBlank()) camposFaltantes.add("Precio Venta")
        if (stock.isBlank()) camposFaltantes.add("Stock")
        if (stockMinimo.isBlank()) camposFaltantes.add("Stock Mínimo")
        if (selectedCategoria == null) camposFaltantes.add("Categoría")
        if (selectedMarca == null) camposFaltantes.add("Marca")
        if (selectedProveedor == null) camposFaltantes.add("Proveedor")
        return if (camposFaltantes.isNotEmpty()) {
            errorMessage = "Complete todos los campos: ${camposFaltantes.joinToString(", ")}"
            false
        } else {
            errorMessage = null
            true
        }
    }

    fun submit() {
        if (!validate()) return
        val request = ProductoRequest(
            codigo = codigo,
            nombre = nombre,
            descripcion = descripcion,
            precioCompra = precioCompra.toDoubleOrNull() ?: 0.0,
            precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
            stock = stock.toIntOrNull() ?: 0,
            stockMinimo = stockMinimo.toIntOrNull() ?: 0,
            imagen = if (imagenUrl.isBlank()) null else imagenUrl,
            idCategoria = selectedCategoria!!.idCategoria!!,
            idMarca = selectedMarca!!.idMarca!!,
            idProveedor = selectedProveedor!!.idProveedor!!
        )
        viewModel.createProduct(request)
        navController.popBackStack()
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
            OutlinedTextField(value = codigo, onValueChange = { codigo = it; errorMessage = null }, label = { Text("Código *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = nombre, onValueChange = { nombre = it; errorMessage = null }, label = { Text("Nombre *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = precioCompra, onValueChange = { precioCompra = it; errorMessage = null }, label = { Text("Precio Compra *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = precioVenta, onValueChange = { precioVenta = it; errorMessage = null }, label = { Text("Precio Venta *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = stock, onValueChange = { stock = it; errorMessage = null }, label = { Text("Stock *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = stockMinimo, onValueChange = { stockMinimo = it; errorMessage = null }, label = { Text("Stock Mínimo *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL Imagen") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))

            ExposedDropdownMenuBox(
                expanded = categoriaExpanded,
                onExpandedChange = { categoriaExpanded = !categoriaExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategoria?.nombre ?: "Seleccionar categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = categoriaExpanded, onDismissRequest = { categoriaExpanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = { selectedCategoria = cat; errorMessage = null; categoriaExpanded = false }
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
                    label = { Text("Marca *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = marcaExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = marcaExpanded, onDismissRequest = { marcaExpanded = false }) {
                    marcas.forEach { marca ->
                        DropdownMenuItem(
                            text = { Text(marca.nombre) },
                            onClick = { selectedMarca = marca; errorMessage = null; marcaExpanded = false }
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
                    label = { Text("Proveedor *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = proveedorExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = proveedorExpanded, onDismissRequest = { proveedorExpanded = false }) {
                    proveedores.forEach { prov ->
                        DropdownMenuItem(
                            text = { Text(prov.razonSocial) },
                            onClick = { selectedProveedor = prov; errorMessage = null; proveedorExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Button(
                onClick = { submit() },
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
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories by viewModel.categories.collectAsState()
    val marcas by viewModel.marcas.collectAsState()
    val proveedores by viewModel.proveedores.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.loadMarcas()
        viewModel.loadProveedores()
    }

    var productLoaded by remember { mutableStateOf(false) }

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
            productLoaded = true
        }
    }

    LaunchedEffect(productLoaded, categories, marcas, proveedores) {
        if (productLoaded) {
            viewModel.getProductById(id)?.let { product ->
                selectedCategoria = categories.find { it.nombre == product.categoria }
                if (selectedCategoria == null && categories.isNotEmpty()) selectedCategoria = categories.first()
                selectedMarca = marcas.find { it.nombre == product.marca }
                if (selectedMarca == null && marcas.isNotEmpty()) selectedMarca = marcas.first()
                selectedProveedor = proveedores.find { it.razonSocial == product.proveedor }
                if (selectedProveedor == null && proveedores.isNotEmpty()) selectedProveedor = proveedores.first()
            }
        }
    }

    fun validate(): Boolean {
        val camposFaltantes = mutableListOf<String>()
        if (codigo.isBlank()) camposFaltantes.add("Código")
        if (nombre.isBlank()) camposFaltantes.add("Nombre")
        if (precioCompra.isBlank()) camposFaltantes.add("Precio Compra")
        if (precioVenta.isBlank()) camposFaltantes.add("Precio Venta")
        if (stock.isBlank()) camposFaltantes.add("Stock")
        if (stockMinimo.isBlank()) camposFaltantes.add("Stock Mínimo")
        return if (camposFaltantes.isNotEmpty()) {
            errorMessage = "Complete todos los campos: ${camposFaltantes.joinToString(", ")}"
            false
        } else {
            errorMessage = null
            true
        }
    }

    fun submit() {
        if (!validate()) return
        val request = ProductoRequest(
            codigo = codigo,
            nombre = nombre,
            descripcion = descripcion,
            precioCompra = precioCompra.toDoubleOrNull() ?: 0.0,
            precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
            stock = stock.toIntOrNull() ?: 0,
            stockMinimo = stockMinimo.toIntOrNull() ?: 0,
            imagen = if (imagenUrl.isBlank()) null else imagenUrl,
            idCategoria = selectedCategoria?.idCategoria,
            idMarca = selectedMarca?.idMarca,
            idProveedor = selectedProveedor?.idProveedor
        )
        viewModel.updateProduct(id, request)
        navController.popBackStack()
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
            OutlinedTextField(value = codigo, onValueChange = { codigo = it; errorMessage = null }, label = { Text("Código *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = nombre, onValueChange = { nombre = it; errorMessage = null }, label = { Text("Nombre *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = precioCompra, onValueChange = { precioCompra = it; errorMessage = null }, label = { Text("Precio Compra *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = precioVenta, onValueChange = { precioVenta = it; errorMessage = null }, label = { Text("Precio Venta *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = stock, onValueChange = { stock = it; errorMessage = null }, label = { Text("Stock *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = stockMinimo, onValueChange = { stockMinimo = it; errorMessage = null }, label = { Text("Stock Mínimo *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL Imagen") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))

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
                            onClick = { selectedCategoria = cat; errorMessage = null; categoriaExpanded = false }
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
                            onClick = { selectedMarca = marca; errorMessage = null; marcaExpanded = false }
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
                            onClick = { selectedProveedor = prov; errorMessage = null; proveedorExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Button(
                onClick = { submit() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Actualizar Producto", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
