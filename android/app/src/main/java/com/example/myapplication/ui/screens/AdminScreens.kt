package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.RegisterRequest
import com.example.myapplication.model.UpdateUserRequest
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.viewmodel.UserViewModel
import com.example.myapplication.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(viewModel: UserViewModel, navController: NavController) {
    val users by viewModel.users.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadUsers() }

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
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
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
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val availableRoles by viewModel.availableRoles.collectAsState()
    val selectedRoles = remember { mutableStateListOf<Long>() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) { viewModel.loadAvailableRoles() }

    fun validate(): Boolean {
        val faltantes = mutableListOf<String>()
        if (nombres.isBlank()) faltantes.add("Nombres")
        if (apellidos.isBlank()) faltantes.add("Apellidos")
        if (dni.isBlank()) faltantes.add("DNI")
        if (correo.isBlank()) faltantes.add("Correo")
        else if (!correo.contains("@")) { errorMessage = "El correo debe contener @"; return false }
        if (password.isBlank()) faltantes.add("Contraseña")
        return if (faltantes.isNotEmpty()) {
            errorMessage = "Complete todos los campos: ${faltantes.joinToString(", ")}"
            false
        } else {
            errorMessage = null
            true
        }
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
            OutlinedTextField(value = nombres, onValueChange = { nombres = it; errorMessage = null }, label = { Text("Nombres *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it; errorMessage = null }, label = { Text("Apellidos *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = dni, onValueChange = { dni = it; errorMessage = null }, label = { Text("DNI *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = correo, onValueChange = { correo = it; errorMessage = null }, label = { Text("Correo Electrónico *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = password, onValueChange = { password = it; errorMessage = null }, label = { Text("Contraseña *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
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

            if (errorMessage != null) {
                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
                }
            }

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
    var errorMessage by remember { mutableStateOf<String?>(null) }
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
        val faltantes = mutableListOf<String>()
        if (nombres.isBlank()) faltantes.add("Nombres")
        if (apellidos.isBlank()) faltantes.add("Apellidos")
        if (dni.isBlank()) faltantes.add("DNI")
        return if (faltantes.isNotEmpty()) {
            errorMessage = "Complete todos los campos: ${faltantes.joinToString(", ")}"
            false
        } else {
            errorMessage = null
            true
        }
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
            OutlinedTextField(value = nombres, onValueChange = { nombres = it; errorMessage = null }, label = { Text("Nombres *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it; errorMessage = null }, label = { Text("Apellidos *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = dni, onValueChange = { dni = it; errorMessage = null }, label = { Text("DNI *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
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

            if (errorMessage != null) {
                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
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
        if (data == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val resumen = data!!.resumen
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(icon = Icons.Default.TrendingUp, title = "Ingresos del Mes", value = "S/ ${resumen.ingresosDelMes}", color = MaterialTheme.colorScheme.primary)
                DashboardCard(icon = Icons.Default.ShoppingCart, title = "Ventas del Mes", value = "${resumen.totalVentasDelMes}", color = MaterialTheme.colorScheme.primary)
                DashboardCard(icon = Icons.Default.PendingActions, title = "Pedidos Pendientes", value = "${resumen.totalPedidosPendientes}", color = MaterialTheme.colorScheme.tertiary)
                DashboardCard(icon = Icons.Default.LocalShipping, title = "Envíos Pendientes", value = "${resumen.totalEnviosPendientes}", color = Color(0xFFFFA000))
                DashboardCard(icon = Icons.Default.Inventory, title = "Total Productos", value = "${resumen.totalProductos}", color = MaterialTheme.colorScheme.secondary)
                DashboardCard(icon = Icons.Default.People, title = "Total Usuarios", value = "${resumen.totalUsuarios}", color = MaterialTheme.colorScheme.secondary)

                if (data!!.productosStockBajo.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Productos con Stock Bajo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    data!!.productosStockBajo.forEach { prod ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(prod.nombre, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text("Stock: ${prod.stock} (Mín: ${prod.stockMinimo})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}
