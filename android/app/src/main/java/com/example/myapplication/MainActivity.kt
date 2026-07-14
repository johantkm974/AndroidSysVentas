package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.datastore.SessionManager
import com.example.myapplication.navigation.NavGraph
import com.example.myapplication.navigation.Screen
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.repository.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val sessionManager = SessionManager(this)
        val apiService = RetrofitClient.getApiService(this)
        val authRepository = AuthRepository(apiService)
        val productRepository = ProductRepository(apiService)
        val userRepository = UserRepository(apiService)
        val orderRepository = OrderRepository(apiService)
        val ventaRepository = VentaRepository(apiService)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    val loginViewModel: LoginViewModel = viewModel(factory = createFactory { LoginViewModel(authRepository, sessionManager) })
                    val userViewModel: UserViewModel = viewModel(factory = createFactory { UserViewModel(userRepository) })
                    val productViewModel: ProductViewModel = viewModel(factory = createFactory { ProductViewModel(productRepository) })
                    val orderViewModel: OrderViewModel = viewModel(factory = createFactory { OrderViewModel(orderRepository) })
                    val dashboardViewModel: DashboardViewModel = viewModel(factory = createFactory { DashboardViewModel(apiService) })
                    val categoriaViewModel: CategoriaViewModel = viewModel(factory = createFactory { CategoriaViewModel(apiService) })
                    val marcaViewModel: MarcaViewModel = viewModel(factory = createFactory { MarcaViewModel(apiService) })
                    val proveedorViewModel: ProveedorViewModel = viewModel(factory = createFactory { ProveedorViewModel(apiService) })
                    val cartViewModel: CartViewModel = viewModel(factory = createFactory { CartViewModel(orderRepository) })
                    val ventaViewModel: VentaViewModel = viewModel(factory = createFactory { VentaViewModel(ventaRepository) })

                    NavGraph(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        userViewModel = userViewModel,
                        productViewModel = productViewModel,
                        orderViewModel = orderViewModel,
                        dashboardViewModel = dashboardViewModel,
                        categoriaViewModel = categoriaViewModel,
                        marcaViewModel = marcaViewModel,
                        proveedorViewModel = proveedorViewModel,
                        cartViewModel = cartViewModel,
                        ventaViewModel = ventaViewModel,
                        onLogout = {
                            MainScope().launch {
                                sessionManager.clearSession()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private inline fun <reified T : ViewModel> createFactory(crossinline creator: () -> T): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator() as T
            }
        }
    }
}
