package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(navController: NavController, onLogout: () -> Unit) {
    RoleBaseScreen(
        title = "Panel de Cliente",
        color = Color(0xFF2196F3),
        onLogout = onLogout,
        onCatalogClick = { navController.navigate(Screen.ProductList.route) }
    ) {
        Button(
            onClick = { navController.navigate(Screen.OrderList.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Mis Pedidos")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerHomeScreen(navController: NavController, onLogout: () -> Unit) {
    RoleBaseScreen(
        title = "Panel de Vendedor",
        color = Color(0xFF4CAF50),
        onLogout = onLogout,
        onCatalogClick = { navController.navigate(Screen.ProductList.route) }
    ) {
        Button(
            onClick = { navController.navigate(Screen.OrderList.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gestionar Pedidos")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate(Screen.VentaList.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Ventas Realizadas")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockerHomeScreen(navController: NavController, onLogout: () -> Unit) {
    RoleBaseScreen(
        title = "Panel de Almacenero",
        color = Color(0xFFFF9800),
        onLogout = onLogout,
        onCatalogClick = { navController.navigate(Screen.InventoryManagement.route) }
    ) {
        Button(onClick = { navController.navigate(Screen.CategoriaManagement.route) }, modifier = Modifier.fillMaxWidth()) { Text("Gestionar Categorías") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.MarcaManagement.route) }, modifier = Modifier.fillMaxWidth()) { Text("Gestionar Marcas") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.ProveedorManagement.route) }, modifier = Modifier.fillMaxWidth()) { Text("Gestionar Proveedores") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(navController: NavController, onLogout: () -> Unit) {
    RoleBaseScreen(
        title = "Panel de Administrador",
        color = Color(0xFFF44336),
        onLogout = onLogout,
        onCatalogClick = { navController.navigate(Screen.ProductList.route) }
    ) {
        Button(onClick = { navController.navigate(Screen.Dashboard.route) }, modifier = Modifier.fillMaxWidth()) { Text("Ver Dashboard") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.UserManagement.route) }, modifier = Modifier.fillMaxWidth()) { Text("Gestionar Usuarios") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.InventoryManagement.route) }, modifier = Modifier.fillMaxWidth()) { Text("Gestionar Inventario") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.OrderList.route) }, modifier = Modifier.fillMaxWidth()) { Text("Gestionar Pedidos") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.VentaList.route) }, modifier = Modifier.fillMaxWidth()) { Text("Gestionar Ventas") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleBaseScreen(
    title: String,
    color: Color,
    onLogout: () -> Unit,
    onCatalogClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Salir", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = color,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            content()
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCatalogClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Ir a Catálogo / Inventario")
            }
        }
    }
}
