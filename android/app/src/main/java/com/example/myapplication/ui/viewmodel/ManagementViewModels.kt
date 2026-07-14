package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// --- User Management ViewModel ---
class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _users = MutableStateFlow<List<UsuarioResponse>>(emptyList())
    val users: StateFlow<List<UsuarioResponse>> = _users

    private val _availableRoles = MutableStateFlow<List<RoleResponse>>(emptyList())
    val availableRoles: StateFlow<List<RoleResponse>> = _availableRoles

    fun loadUsers() {
        viewModelScope.launch {
            repository.listUsers().onSuccess { _users.value = it }
        }
    }

    fun loadAvailableRoles() {
        val mockRoles = listOf(
            RoleResponse(1, "ROLE_ADMIN", "Administrador"),
            RoleResponse(2, "ROLE_VENDEDOR", "Vendedor"),
            RoleResponse(3, "ROLE_ALMACENERO", "Almacenero"),
            RoleResponse(4, "ROLE_CLIENTE", "Cliente")
        )
        _availableRoles.value = mockRoles
        
        viewModelScope.launch {
            repository.listRoles().onSuccess { 
                if (it.isNotEmpty()) _availableRoles.value = it 
            }
        }
    }

    fun deleteUser(id: Long) {
        viewModelScope.launch {
            repository.deleteUser(id).onSuccess { loadUsers() }
        }
    }

    fun updateUser(id: Long, request: UpdateUserRequest) {
        viewModelScope.launch {
            repository.updateUser(id, request).onSuccess { loadUsers() }
        }
    }

    fun createUser(request: RegisterRequest) {
        viewModelScope.launch {
            repository.createUser(request).onSuccess { loadUsers() }
        }
    }

    suspend fun getUserById(id: Long): UsuarioResponse? {
        return repository.getUserById(id).getOrNull()
    }
}

// --- Order Management ViewModel ---
class OrderViewModel(
    private val repository: OrderRepository,
    private val ventaRepository: VentaRepository
) : ViewModel() {
    private val _orders = MutableStateFlow<List<PedidoResponse>>(emptyList())
    val orders: StateFlow<List<PedidoResponse>> = _orders

    fun loadAllOrders() {
        viewModelScope.launch {
            repository.listAllOrders().onSuccess { _orders.value = it }
        }
    }

    fun loadMyOrders() {
        viewModelScope.launch {
            repository.listMyOrders().onSuccess { _orders.value = it }
        }
    }

    fun updateStatus(id: Long, idEstado: Long, isAdmin: Boolean) {
        viewModelScope.launch {
            repository.updateOrderStatus(id, idEstado).onSuccess {
                if (isAdmin) loadAllOrders() else loadMyOrders()
            }
        }
    }

    fun confirmOrder(id: Long, isAdmin: Boolean) {
        viewModelScope.launch {
            val result = repository.updateOrderStatus(id, 2)
            if (result.isSuccess) {
                repository.listAllOrders().onSuccess { _orders.value = it }
            }
        }
    }

    fun processSaleAndConfirm(id: Long) {
        viewModelScope.launch {
            ventaRepository.processSale(VentaRequest(id, 5)).onSuccess {
                loadAllOrders()
            }
        }
    }

    fun cancelOrder(id: Long) {
        viewModelScope.launch {
            repository.cancelOrder(id).onSuccess { loadMyOrders() }
        }
    }
}

// --- Sales Management ViewModel ---
class VentaViewModel(private val repository: VentaRepository) : ViewModel() {
    private val _sales = MutableStateFlow<List<VentaResponse>>(emptyList())
    val sales: StateFlow<List<VentaResponse>> = _sales

    fun loadSales() {
        viewModelScope.launch {
            repository.listSales().onSuccess { _sales.value = it }
        }
    }

    fun processSale(pedidoId: Long, metodoPagoId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.processSale(VentaRequest(pedidoId, metodoPagoId)).onSuccess {
                loadSales()
                onSuccess()
            }
        }
    }

    fun annulSale(id: Long) {
        viewModelScope.launch {
            repository.cancelSale(id).onSuccess { loadSales() }
        }
    }
}

// --- Dashboard ViewModel ---
class DashboardViewModel(private val apiService: ApiService) : ViewModel() {
    private val _dashboardData = MutableStateFlow<DashboardResponse?>(null)
    val dashboardData: StateFlow<DashboardResponse?> = _dashboardData

    fun loadDashboard() {
        viewModelScope.launch {
            try { _dashboardData.value = apiService.dashboard() } catch (e: Exception) {}
        }
    }
}
