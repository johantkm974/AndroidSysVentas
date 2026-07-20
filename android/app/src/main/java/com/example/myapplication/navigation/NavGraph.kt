package com.example.myapplication.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.viewmodel.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object ClientHome : Screen("client_home")
    object SellerHome : Screen("seller_home")
    object StockerHome : Screen("stocker_home")
    object RepartidorHome : Screen("repartidor_home")
    object AdminHome : Screen("admin_home")
    object ProductList : Screen("product_list")
    object Cart : Screen("cart")
    object UserManagement : Screen("user_management")
    object EditUser : Screen("edit_user/{id}") {
        fun createRoute(id: Long) = "edit_user/$id"
    }
    object CreateUser : Screen("create_user")
    object InventoryManagement : Screen("inventory_management")
    object AddProduct : Screen("add_product")
    object EditProduct : Screen("edit_product/{id}") {
        fun createRoute(id: Long) = "edit_product/$id"
    }
    object OrderList : Screen("order_list")
    object OrderListFiltered : Screen("order_list_filtered/{filter}") {
        fun createRoute(filter: String) = "order_list_filtered/$filter"
    }
    object VentaList : Screen("venta_list")
    object Dashboard : Screen("dashboard")
    object CategoriaManagement : Screen("categoria_management")
    object MarcaManagement : Screen("marca_management")
    object ProveedorManagement : Screen("proveedor_management")
    object Register : Screen("register")
    object DeliveryList : Screen("delivery_list")
    object DeliveryDetail : Screen("delivery_detail/{id}") {
        fun createRoute(id: Long) = "delivery_detail/$id"
    }
    object AdminEnvios : Screen("admin_envios")
    object OrderDetail : Screen("order_detail/{pedidoId}") {
        fun createRoute(pedidoId: Long) = "order_detail/$pedidoId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    userViewModel: UserViewModel,
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel,
    dashboardViewModel: DashboardViewModel,
    categoriaViewModel: CategoriaViewModel,
    marcaViewModel: MarcaViewModel,
    proveedorViewModel: ProveedorViewModel,
    cartViewModel: CartViewModel,
    ventaViewModel: VentaViewModel,
    deliveryViewModel: DeliveryViewModel,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { it / 4 }
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { -it / 4 }
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200))
        }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                navController = navController,
                onLoginSuccess = { roles ->
                    val destination = when {
                        roles.contains("ROLE_ADMIN") -> Screen.AdminHome.route
                        roles.contains("ROLE_VENDEDOR") -> Screen.SellerHome.route
                        roles.contains("ROLE_ALMACENERO") -> Screen.StockerHome.route
                        roles.contains("ROLE_REPARTIDOR") -> Screen.RepartidorHome.route
                        else -> Screen.ClientHome.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.ClientHome.route) { ClientHomeScreen(navController, onLogout) }
        composable(Screen.SellerHome.route) { SellerHomeScreen(navController, onLogout) }
        composable(Screen.StockerHome.route) { StockerHomeScreen(navController, onLogout) }
        composable(Screen.RepartidorHome.route) { RepartidorHomeScreen(navController, onLogout) }
        composable(Screen.AdminHome.route) { AdminHomeScreen(navController, onLogout) }

        composable(Screen.UserManagement.route) { UserManagementScreen(userViewModel, navController) }
        composable(
            route = Screen.EditUser.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            EditUserScreen(id, userViewModel, navController)
        }
        composable(Screen.CreateUser.route) { CreateUserScreen(userViewModel, navController) }
        composable(Screen.InventoryManagement.route) { InventoryManagementScreen(productViewModel, navController) }
        composable(Screen.AddProduct.route) { AddProductScreen(productViewModel, navController) }
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            EditProductScreen(id, productViewModel, navController)
        }
        composable(Screen.OrderList.route) { 
            // Obtenemos los roles del usuario logueado para saber qué lista mostrar
            val userState by loginViewModel.uiState.collectAsState()
            val isAdminOrSeller = if (userState is LoginUiState.Success) {
                val roles = (userState as LoginUiState.Success).roles
                roles.contains("ROLE_ADMIN") || roles.contains("ROLE_VENDEDOR")
            } else {
                false
            }
            
            OrderListScreen(orderViewModel, navController, isAdminOrSeller)
        }
        composable(
            route = Screen.OrderListFiltered.route,
            arguments = listOf(navArgument("filter") { type = NavType.StringType })
        ) { backStackEntry ->
            val filter = backStackEntry.arguments?.getString("filter") ?: ""
            OrderListScreen(orderViewModel, navController, isAdminOrSeller = true, initialFilter = filter)
        }
        composable(Screen.VentaList.route) { VentaListScreen(ventaViewModel, navController) }
        composable(Screen.Dashboard.route) { AdminDashboardScreen(dashboardViewModel, navController) }
        composable(Screen.Cart.route) { CartScreen(cartViewModel, navController) }
        composable(Screen.CategoriaManagement.route) { CategoriaManagementScreen(categoriaViewModel, navController) }
        composable(Screen.MarcaManagement.route) { MarcaManagementScreen(marcaViewModel, navController) }
        composable(Screen.ProveedorManagement.route) { ProveedorManagementScreen(proveedorViewModel, navController) }
        composable(Screen.Register.route) { RegisterScreen(loginViewModel, navController) }
        composable(Screen.ProductList.route) { 
            ProductListScreen(productViewModel, cartViewModel, navController, onLogout) 
        }
        composable(Screen.DeliveryList.route) {
            DeliveryListScreen(deliveryViewModel, navController)
        }
        composable(
            route = Screen.DeliveryDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val userState by loginViewModel.uiState.collectAsState()
            val isRepartidor = if (userState is LoginUiState.Success) {
                (userState as LoginUiState.Success).roles.contains("ROLE_REPARTIDOR")
            } else {
                false
            }
            DeliveryDetailScreen(id, deliveryViewModel, navController, isRepartidor)
        }
        composable(Screen.AdminEnvios.route) {
            AdminEnviosScreen(deliveryViewModel, navController)
        }
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("pedidoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getLong("pedidoId") ?: 0L
            OrderDetailScreen(pedidoId, orderViewModel, navController)
        }
    }
}
