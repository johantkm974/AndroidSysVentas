package com.example.myapplication.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import android.util.Patterns
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.RegisterRequest
import com.example.myapplication.model.UpdateUserRequest
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.ErrorScreen
import com.example.myapplication.ui.components.LoadingScreen
import com.example.myapplication.ui.viewmodel.UserViewModel
import com.example.myapplication.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(viewModel: UserViewModel, navController: NavController) {
    val users by viewModel.users.collectAsState()
    val availableRoles by viewModel.availableRoles.collectAsState()
    var searchDni by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadUsers(); viewModel.loadAvailableRoles() }

    val filteredUsers = users.filter { user ->
        val matchesDni = searchDni.isBlank() || user.dni.contains(searchDni)
        val matchesRole = selectedRole == null || user.roles.contains(selectedRole)
        matchesDni && matchesRole
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
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
                onClick = { navController.navigate(Screen.CreateUser.route) },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Usuario")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchDni,
                onValueChange = { searchDni = it },
                placeholder = { Text("Buscar por DNI...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedRole == null,
                    onClick = { selectedRole = null },
                    label = { Text("Todos") }
                )
                availableRoles.forEach { role ->
                    FilterChip(
                        selected = selectedRole == role.nombre,
                        onClick = { selectedRole = if (selectedRole == role.nombre) null else role.nombre },
                        label = { Text(role.descripcion ?: role.nombre) }
                    )
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUsers) { user ->
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
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${user.nombres} ${user.apellidos}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(user.correo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("DNI: ${user.dni}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (!user.telefono.isNullOrBlank()) {
                                    Text("Tel: ${user.telefono}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (!user.direccion.isNullOrBlank()) {
                                    Text(user.direccion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    "Roles: ${user.roles.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Row {
                                IconButton(onClick = { navController.navigate(Screen.EditUser.createRoute(user.idUsuario)) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.deleteUser(user.idUsuario) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(viewModel: UserViewModel, navController: NavController) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf<String?>(null) }
    var apellidoError by remember { mutableStateOf<String?>(null) }
    var dniError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val availableRoles by viewModel.availableRoles.collectAsState()
    val selectedRoles = remember { mutableStateListOf<Long>() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) { viewModel.loadAvailableRoles() }

    fun validate(): Boolean {
        var valid = true
        if (nombres.isBlank()) { nombreError = "Campo obligatorio"; valid = false } else { nombreError = null }
        if (apellidos.isBlank()) { apellidoError = "Campo obligatorio"; valid = false } else { apellidoError = null }
        if (dni.isBlank()) { dniError = "Campo obligatorio"; valid = false }
        else if (dni.length < 8) { dniError = "Debe tener al menos 8 dígitos"; valid = false }
        else { dniError = null }
        if (correo.isBlank()) { correoError = "Campo obligatorio"; valid = false }
        else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { correoError = "Correo inválido"; valid = false }
        else { correoError = null }
        if (password.isBlank()) { passwordError = "Campo obligatorio"; valid = false }
        else if (password.length < 6) { passwordError = "Mínimo 6 caracteres"; valid = false }
        else { passwordError = null }
        return valid
    }

    fun submit() {
        if (!validate()) return
        val finalRoles = if (selectedRoles.isEmpty()) listOf(4L) else selectedRoles.toList()
        val request = RegisterRequest(
            nombres = nombres, apellidos = apellidos, dni = dni, correo = correo,
            password = password, telefono = if (telefono.isBlank()) null else telefono,
            direccion = if (direccion.isBlank()) null else direccion, idRoles = finalRoles
        )
        viewModel.createUser(request)
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Usuario") },
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
            OutlinedTextField(value = nombres, onValueChange = { nombres = it; nombreError = null }, label = { Text("Nombres *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                isError = nombreError != null, supportingText = nombreError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it; apellidoError = null }, label = { Text("Apellidos *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                isError = apellidoError != null, supportingText = apellidoError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = dni, onValueChange = { dni = it.filter { c -> c.isDigit() }.take(8); dniError = null }, label = { Text("DNI *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                isError = dniError != null, supportingText = dniError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = correo, onValueChange = { correo = it; correoError = null }, label = { Text("Correo Electrónico *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                isError = correoError != null, supportingText = correoError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = password, onValueChange = { password = it; passwordError = null }, label = { Text("Contraseña *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                isError = passwordError != null, supportingText = passwordError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = telefono, onValueChange = { telefono = it.filter { c -> c.isDigit() }.take(9) }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { submit() }))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Asignar Roles:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            availableRoles.forEach { role ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedRoles.contains(role.idRol)) selectedRoles.remove(role.idRol)
                            else selectedRoles.add(role.idRol)
                        }
                        .padding(vertical = 2.dp)
                ) {
                    Checkbox(checked = selectedRoles.contains(role.idRol), onCheckedChange = null)
                    Text(role.nombre, modifier = Modifier.padding(start = 8.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { submit() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Crear Usuario", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen(id: Long, viewModel: UserViewModel, navController: NavController) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(true) }
    var nombreError by remember { mutableStateOf<String?>(null) }
    var apellidoError by remember { mutableStateOf<String?>(null) }
    var dniError by remember { mutableStateOf<String?>(null) }
    val availableRoles by viewModel.availableRoles.collectAsState()
    val selectedRoles = remember { mutableStateListOf<Long>() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(id) {
        viewModel.loadAvailableRoles()
        viewModel.getUserById(id)?.let { user ->
            nombres = user.nombres
            apellidos = user.apellidos
            dni = user.dni
            telefono = user.telefono ?: ""
            direccion = user.direccion ?: ""
            activo = user.activo
        }
    }

    fun validate(): Boolean {
        var valid = true
        if (nombres.isBlank()) { nombreError = "Campo obligatorio"; valid = false } else { nombreError = null }
        if (apellidos.isBlank()) { apellidoError = "Campo obligatorio"; valid = false } else { apellidoError = null }
        if (dni.isBlank()) { dniError = "Campo obligatorio"; valid = false }
        else if (dni.length < 8) { dniError = "Debe tener al menos 8 dígitos"; valid = false }
        else { dniError = null }
        return valid
    }

    fun submit() {
        if (!validate()) return
        val finalRoles = if (selectedRoles.isEmpty()) listOf(4L) else selectedRoles.toList()
        val request = UpdateUserRequest(
            nombres = nombres, apellidos = apellidos, dni = dni,
            telefono = telefono, direccion = direccion, activo = activo, idRoles = finalRoles
        )
        viewModel.updateUser(id, request)
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Usuario #$id") },
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
            OutlinedTextField(value = nombres, onValueChange = { nombres = it; nombreError = null }, label = { Text("Nombres *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                isError = nombreError != null, supportingText = nombreError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it; apellidoError = null }, label = { Text("Apellidos *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                isError = apellidoError != null, supportingText = apellidoError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = dni, onValueChange = { dni = it.filter { c -> c.isDigit() }.take(8); dniError = null }, label = { Text("DNI *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                isError = dniError != null, supportingText = dniError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = telefono, onValueChange = { telefono = it.filter { c -> c.isDigit() }.take(9) }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { submit() }))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Checkbox(checked = activo, onCheckedChange = { activo = it })
                Text("Usuario Activo")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Roles del Usuario:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            availableRoles.forEach { role ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable {
                        if (selectedRoles.contains(role.idRol)) selectedRoles.remove(role.idRol)
                        else selectedRoles.add(role.idRol)
                    }.padding(vertical = 2.dp)
                ) {
                    Checkbox(checked = selectedRoles.contains(role.idRol), onCheckedChange = null)
                    Text(role.nombre, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { submit() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Cambios", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(viewModel: DashboardViewModel, navController: NavController) {
    val data by viewModel.dashboardData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isConnectionError by viewModel.isConnectionError.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadDashboard() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Administrador") },
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
        when {
            isLoading -> LoadingScreen("Cargando dashboard...")
            errorMessage != null -> ErrorScreen(
                message = errorMessage ?: "Error",
                isConnectionError = isConnectionError
            ) { viewModel.loadDashboard() }
            data == null -> ErrorScreen(message = "No se pudieron cargar los datos") { viewModel.loadDashboard() }
            else -> {
                val resumen = data!!.resumen
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Resumen General",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardStatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.TrendingUp,
                            title = "Ingresos",
                            value = "S/ ${resumen.ingresosDelMes}",
                            color = Color(0xFF2E7D32)
                        )
                        DashboardStatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.ShoppingCart,
                            title = "Ventas",
                            value = "${resumen.totalVentasDelMes}",
                            color = Color(0xFF1565C0)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardStatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Inventory,
                            title = "Productos",
                            value = "${resumen.totalProductos}",
                            color = Color(0xFF6A1B9A)
                        )
                        DashboardStatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.People,
                            title = "Usuarios",
                            value = "${resumen.totalUsuarios}",
                            color = Color(0xFF00838F)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Accesos Rápidos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DashboardActionCard(
                        icon = Icons.Default.PendingActions,
                        title = "Pedidos Pendientes",
                        subtitle = "${resumen.totalPedidosPendientes} pedidos por procesar",
                        color = Color(0xFFFFA000),
                        onClick = { navController.navigate(Screen.OrderListFiltered.createRoute("PENDIENTE")) }
                    )
                    DashboardActionCard(
                        icon = Icons.Default.LocalShipping,
                        title = "Envíos Pendientes",
                        subtitle = "${resumen.totalEnviosPendientes} envíos por entregar",
                        color = Color(0xFFE65100),
                        onClick = { navController.navigate(Screen.AdminEnvios.route) }
                    )
                    DashboardActionCard(
                        icon = Icons.Default.Inventory,
                        title = "Gestión de Productos",
                        subtitle = "Administrar catálogo",
                        color = Color(0xFF6A1B9A),
                        onClick = { navController.navigate(Screen.InventoryManagement.route) }
                    )
                    DashboardActionCard(
                        icon = Icons.Default.People,
                        title = "Gestión de Usuarios",
                        subtitle = "Administrar usuarios",
                        color = Color(0xFF00838F),
                        onClick = { navController.navigate(Screen.UserManagement.route) }
                    )

                    if (data!!.productosStockBajo.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Stock Bajo (${data!!.productosStockBajo.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        data!!.productosStockBajo.forEach { prod ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(prod.nombre, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text("Código: ${prod.codigo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${prod.stock}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                        Text("Mín: ${prod.stockMinimo}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = modifier
            .height(100.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.8f))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun DashboardActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
