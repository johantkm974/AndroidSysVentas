package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(navController: NavController, onLogout: () -> Unit) {
    RoleBaseScreen(
        title = "Panel de Cliente",
        color = MaterialTheme.colorScheme.primary,
        onLogout = onLogout
    ) {
        MenuCard(
            icon = Icons.Default.ShoppingCart,
            title = "Catálogo de Productos",
            subtitle = "Explora y compra productos",
            onClick = { navController.navigate(Screen.ProductList.route) }
        )
        MenuCard(
            icon = Icons.AutoMirrored.Filled.ListAlt,
            title = "Mis Pedidos",
            subtitle = "Consulta el estado de tus pedidos",
            onClick = { navController.navigate(Screen.OrderList.route) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerHomeScreen(navController: NavController, onLogout: () -> Unit) {
    RoleBaseScreen(
        title = "Panel de Vendedor",
        color = MaterialTheme.colorScheme.secondary,
        onLogout = onLogout
    ) {
        MenuCard(
            icon = Icons.Default.ShoppingCart,
            title = "Catálogo de Productos",
            subtitle = "Visualiza productos disponibles",
            onClick = { navController.navigate(Screen.ProductList.route) }
        )
        MenuCard(
            icon = Icons.AutoMirrored.Filled.Assignment,
            title = "Gestionar Pedidos",
            subtitle = "Administra y actualiza pedidos",
            onClick = { navController.navigate(Screen.OrderList.route) }
        )
        MenuCard(
            icon = Icons.Default.PointOfSale,
            title = "Ver Ventas Realizadas",
            subtitle = "Historial de ventas",
            onClick = { navController.navigate(Screen.VentaList.route) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockerHomeScreen(navController: NavController, onLogout: () -> Unit) {
    RoleBaseScreen(
        title = "Panel de Almacenero",
        color = Color(0xFFE65100),
        onLogout = onLogout
    ) {
        MenuCard(
            icon = Icons.Default.Inventory,
            title = "Gestionar Inventario",
            subtitle = "Administra productos y stock",
            onClick = { navController.navigate(Screen.InventoryManagement.route) }
        )
        MenuCard(
            icon = Icons.Default.Category,
            title = "Gestionar Categorías",
            subtitle = "Administra categorías de productos",
            onClick = { navController.navigate(Screen.CategoriaManagement.route) }
        )
        MenuCard(
            icon = Icons.Default.Business,
            title = "Gestionar Marcas",
            subtitle = "Administra marcas de productos",
            onClick = { navController.navigate(Screen.MarcaManagement.route) }
        )
        MenuCard(
            icon = Icons.Default.People,
            title = "Gestionar Proveedores",
            subtitle = "Administra proveedores",
            onClick = { navController.navigate(Screen.ProveedorManagement.route) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(navController: NavController, onLogout: () -> Unit) {
    RoleBaseScreen(
        title = "Panel de Administrador",
        color = Color(0xFFC62828),
        onLogout = onLogout
    ) {
        MenuCard(
            icon = Icons.Default.Dashboard,
            title = "Dashboard",
            subtitle = "Resumen de ventas y estadísticas",
            onClick = { navController.navigate(Screen.Dashboard.route) }
        )
        MenuCard(
            icon = Icons.Default.People,
            title = "Gestionar Usuarios",
            subtitle = "Administra usuarios y roles",
            onClick = { navController.navigate(Screen.UserManagement.route) }
        )
        MenuCard(
            icon = Icons.Default.Inventory,
            title = "Gestionar Inventario",
            subtitle = "Administra productos y stock",
            onClick = { navController.navigate(Screen.InventoryManagement.route) }
        )
        MenuCard(
            icon = Icons.AutoMirrored.Filled.Assignment,
            title = "Gestionar Pedidos",
            subtitle = "Administra y actualiza pedidos",
            onClick = { navController.navigate(Screen.OrderList.route) }
        )
        MenuCard(
            icon = Icons.Default.PointOfSale,
            title = "Gestionar Ventas",
            subtitle = "Historial y anulación de ventas",
            onClick = { navController.navigate(Screen.VentaList.route) }
        )
        MenuCard(
            icon = Icons.Default.Category,
            title = "Gestionar Categorías",
            subtitle = "Administra categorías de productos",
            onClick = { navController.navigate(Screen.CategoriaManagement.route) }
        )
        MenuCard(
            icon = Icons.Default.Business,
            title = "Gestionar Marcas",
            subtitle = "Administra marcas de productos",
            onClick = { navController.navigate(Screen.MarcaManagement.route) }
        )
        MenuCard(
            icon = Icons.Default.People,
            title = "Gestionar Proveedores",
            subtitle = "Administra proveedores",
            onClick = { navController.navigate(Screen.ProveedorManagement.route) }
        )
    }
}

@Composable
fun MenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
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
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleBaseScreen(
    title: String,
    color: Color,
    onLogout: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    @Suppress("DEPRECATION")
                    val logoutIcon = Icons.Default.Logout
                    TextButton(onClick = onLogout) {
                            Icon(
                                logoutIcon,
                            contentDescription = "Salir",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salir", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = color,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}
