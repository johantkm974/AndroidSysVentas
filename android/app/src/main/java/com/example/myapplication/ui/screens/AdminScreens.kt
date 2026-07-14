package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
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
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.CreateUser.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Usuario")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(users) { user ->
                ListItem(
                    headlineContent = { Text("${user.nombres} ${user.apellidos}") },
                    supportingContent = { 
                        Column {
                            Text(user.correo)
                            Text("Roles: ${user.roles.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { navController.navigate(Screen.EditUser.createRoute(user.idUsuario)) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteUser(user.idUsuario) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                )
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
    
    val availableRoles by viewModel.availableRoles.collectAsState()
    val selectedRoles = remember { mutableStateListOf<Long>() }

    LaunchedEffect(Unit) { viewModel.loadAvailableRoles() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Usuario") },
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
            OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Asignar Roles:", style = MaterialTheme.typography.titleMedium)
            
            availableRoles.forEach { role ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedRoles.contains(role.idRol)) {
                                selectedRoles.remove(role.idRol)
                            } else {
                                selectedRoles.add(role.idRol)
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = selectedRoles.contains(role.idRol),
                        onCheckedChange = null // Click handled by Row for better experience
                    )
                    Text(role.nombre, modifier = Modifier.padding(start = 8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val finalRoles = if (selectedRoles.isEmpty()) listOf(4L) else selectedRoles.toList()
                    val request = RegisterRequest(
                        nombres = nombres,
                        apellidos = apellidos,
                        dni = dni,
                        correo = correo,
                        password = password,
                        telefono = if (telefono.isBlank()) null else telefono,
                        direccion = if (direccion.isBlank()) null else direccion,
                        idRoles = finalRoles
                    )
                    viewModel.createUser(request)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Usuario")
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
    
    val availableRoles by viewModel.availableRoles.collectAsState()
    val selectedRoles = remember { mutableStateListOf<Long>() }

    LaunchedEffect(id) {
        viewModel.loadAvailableRoles()
        viewModel.getUserById(id)?.let { user ->
            nombres = user.nombres
            apellidos = user.apellidos
            dni = user.dni
            telefono = user.telefono ?: ""
            direccion = user.direccion ?: ""
            activo = user.activo
            // Nota: Aquí se debería mapear los nombres de roles a IDs si el backend no envía IDs en UsuarioResponse
            // Por simplicidad, asumimos que se gestionan por separado o el backend los resuelve
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Usuario #$id") },
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
            OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                Checkbox(checked = activo, onCheckedChange = { activo = it })
                Text("Usuario Activo")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Roles del Usuario:", style = MaterialTheme.typography.titleMedium)
            availableRoles.forEach { role ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedRoles.contains(role.idRol)) {
                                selectedRoles.remove(role.idRol)
                            } else {
                                selectedRoles.add(role.idRol)
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = selectedRoles.contains(role.idRol),
                        onCheckedChange = null // Click handled by Row for better experience
                    )
                    Text(role.nombre, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val finalRoles = if (selectedRoles.isEmpty()) listOf(4L) else selectedRoles.toList()
                    val request = UpdateUserRequest(
                        nombres = nombres,
                        apellidos = apellidos,
                        dni = dni,
                        telefono = telefono,
                        direccion = direccion,
                        activo = activo,
                        idRoles = finalRoles
                    )
                    viewModel.updateUser(id, request)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cambios")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(viewModel: DashboardViewModel) {
    val data by viewModel.dashboardData.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadDashboard() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard Admin") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            data?.resumen?.let { resumen ->
                DashboardCard("Total Ventas", "S/ ${resumen.ingresosDelMes}")
                DashboardCard("Pedidos Pendientes", "${resumen.totalPedidosPendientes}")
                DashboardCard("Total Usuarios", "${resumen.totalUsuarios}")
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
