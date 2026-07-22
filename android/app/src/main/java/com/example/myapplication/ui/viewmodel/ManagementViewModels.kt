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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isConnectionError = MutableStateFlow(false)
    val isConnectionError: StateFlow<Boolean> = _isConnectionError

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isConnectionError.value = false
            repository.listUsers()
                .onSuccess { _users.value = it }
                .onFailure { e ->
                    _errorMessage.value = HttpErrorParser.parse(e)
                    _isConnectionError.value = HttpErrorParser.isConnectionError(e)
                }
            _isLoading.value = false
        }
    }

    fun loadAvailableRoles() {
        val mockRoles = listOf(
            RoleResponse(1, "ROLE_ADMIN", "Administrador"),
            RoleResponse(2, "ROLE_VENDEDOR", "Vendedor"),
            RoleResponse(3, "ROLE_ALMACENERO", "Almacenero"),
            RoleResponse(4, "ROLE_CLIENTE", "Cliente"),
            RoleResponse(5, "ROLE_REPARTIDOR", "Repartidor")
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
            repository.deleteUser(id)
                .onSuccess { loadUsers() }
                .onFailure { e ->
                    _errorMessage.value = HttpErrorParser.parse(e)
                }
        }
    }

    fun updateUser(id: Long, request: UpdateUserRequest) {
        viewModelScope.launch {
            repository.updateUser(id, request)
                .onSuccess { loadUsers() }
                .onFailure { e ->
                    _errorMessage.value = HttpErrorParser.parse(e)
                }
        }
    }

    fun createUser(request: RegisterRequest) {
        viewModelScope.launch {
            repository.createUser(request)
                .onSuccess { loadUsers() }
                .onFailure { e ->
                    _errorMessage.value = HttpErrorParser.parse(e)
                }
        }
    }

    suspend fun getUserById(id: Long): UsuarioResponse? {
        return repository.getUserById(id).getOrNull()
    }

    fun clearError() {
        _errorMessage.value = null
        _isConnectionError.value = false
    }
}

// --- Order Management ViewModel ---
class OrderViewModel(
    private val repository: OrderRepository,
    private val ventaRepository: VentaRepository,
    private val deliveryRepository: DeliveryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _orders = MutableStateFlow<List<PedidoResponse>>(emptyList())
    val orders: StateFlow<List<PedidoResponse>> = _orders

    private val _repartidores = MutableStateFlow<List<UsuarioResponse>>(emptyList())
    val repartidores: StateFlow<List<UsuarioResponse>> = _repartidores

    private val _assignError = MutableStateFlow<String?>(null)
    val assignError: StateFlow<String?> = _assignError

    private val _assignSuccess = MutableStateFlow<String?>(null)
    val assignSuccess: StateFlow<String?> = _assignSuccess

    private val _selectedEnvio = MutableStateFlow<EnvioResponse?>(null)
    val selectedEnvio: StateFlow<EnvioResponse?> = _selectedEnvio

    private val _tracking = MutableStateFlow<List<SeguimientoResponse>>(emptyList())
    val tracking: StateFlow<List<SeguimientoResponse>> = _tracking

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isConnectionError = MutableStateFlow(false)
    val isConnectionError: StateFlow<Boolean> = _isConnectionError

    fun loadAllOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isConnectionError.value = false
            repository.listAllOrders()
                .onSuccess { _orders.value = it }
                .onFailure { e ->
                    _errorMessage.value = HttpErrorParser.parse(e)
                    _isConnectionError.value = HttpErrorParser.isConnectionError(e)
                }
            _isLoading.value = false
        }
    }

    fun loadMyOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isConnectionError.value = false
            repository.listMyOrders()
                .onSuccess { _orders.value = it }
                .onFailure { e ->
                    _errorMessage.value = HttpErrorParser.parse(e)
                    _isConnectionError.value = HttpErrorParser.isConnectionError(e)
                }
            _isLoading.value = false
        }
    }

    fun loadRepartidores() {
        viewModelScope.launch {
            userRepository.listUsers().onSuccess { users ->
                _repartidores.value = users.filter { it.roles.contains("ROLE_REPARTIDOR") }
            }
        }
    }

    fun updateStatus(id: Long, idEstado: Long, isAdmin: Boolean) {
        viewModelScope.launch {
            repository.updateOrderStatus(id, idEstado)
                .onSuccess {
                    if (isAdmin) loadAllOrders() else loadMyOrders()
                }
                .onFailure { e ->
                    _errorMessage.value = HttpErrorParser.parse(e)
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
            ventaRepository.processSale(VentaRequest(id, 5))
                .onSuccess { loadAllOrders() }
                .onFailure { e ->
                    _errorMessage.value = HttpErrorParser.parse(e)
                }
        }
    }

    fun cancelOrder(id: Long) {
        viewModelScope.launch {
            repository.cancelOrder(id)
                .onSuccess { loadMyOrders() }
                .onFailure { e ->
                    _errorMessage.value = HttpErrorParser.parse(e)
                }
        }
    }

    fun assignRepartidor(pedidoId: Long, repartidorId: Long) {
        viewModelScope.launch {
            _assignError.value = null
            _assignSuccess.value = null
            val envioResult = deliveryRepository.getEnvioByPedido(pedidoId)
            envioResult.onSuccess { envio ->
                deliveryRepository.assignRepartidor(envio.idEnvio, repartidorId)
                    .onSuccess {
                        _assignSuccess.value = "Repartidor asignado con éxito"
                        loadAllOrders()
                    }
                    .onFailure { e ->
                        _assignError.value = HttpErrorParser.parse(e)
                    }
            }.onFailure { e ->
                _assignError.value = HttpErrorParser.parse(e)
            }
        }
    }

    fun clearAssignError() {
        _assignError.value = null
    }

    fun clearAssignSuccess() {
        _assignSuccess.value = null
    }

    fun loadEnvioByPedido(pedidoId: Long) {
        viewModelScope.launch {
            deliveryRepository.getEnvioByPedido(pedidoId)
                .onSuccess { _selectedEnvio.value = it }
        }
    }

    fun loadTracking(envioId: Long) {
        viewModelScope.launch {
            deliveryRepository.getTracking(envioId)
                .onSuccess { _tracking.value = it }
        }
    }

    fun clearEnvioAndTracking() {
        _selectedEnvio.value = null
        _tracking.value = emptyList()
    }

    fun clearError() {
        _errorMessage.value = null
        _isConnectionError.value = false
    }
}

// --- Sales Management ViewModel ---
class VentaViewModel(private val repository: VentaRepository) : ViewModel() {
    private val _sales = MutableStateFlow<List<VentaResponse>>(emptyList())
    val sales: StateFlow<List<VentaResponse>> = _sales

    private val _annulError = MutableStateFlow<String?>(null)
    val annulError: StateFlow<String?> = _annulError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isConnectionError = MutableStateFlow(false)
    val isConnectionError: StateFlow<Boolean> = _isConnectionError

    fun loadSales() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isConnectionError.value = false
            repository.listSales()
                .onSuccess { _sales.value = it }
                .onFailure { e ->
                    _errorMessage.value = HttpErrorParser.parse(e)
                    _isConnectionError.value = HttpErrorParser.isConnectionError(e)
                }
            _isLoading.value = false
        }
    }

    fun processSale(pedidoId: Long, metodoPagoId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.processSale(VentaRequest(pedidoId, metodoPagoId))
                .onSuccess {
                    loadSales()
                    onSuccess()
                }
                .onFailure { e ->
                    _annulError.value = HttpErrorParser.parse(e)
                }
        }
    }

    fun annulSale(id: Long) {
        viewModelScope.launch {
            repository.cancelSale(id)
                .onSuccess {
                    _annulError.value = null
                    loadSales()
                }
                .onFailure { error ->
                    _annulError.value = HttpErrorParser.parse(error)
                }
        }
    }

    fun clearAnnulError() {
        _annulError.value = null
    }

    fun clearError() {
        _errorMessage.value = null
        _isConnectionError.value = false
    }
}

// --- Dashboard ViewModel ---
class DashboardViewModel(private val apiService: ApiService) : ViewModel() {
    private val _dashboardData = MutableStateFlow<DashboardResponse?>(null)
    val dashboardData: StateFlow<DashboardResponse?> = _dashboardData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isConnectionError = MutableStateFlow(false)
    val isConnectionError: StateFlow<Boolean> = _isConnectionError

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isConnectionError.value = false
            try {
                _dashboardData.value = apiService.dashboard()
            } catch (e: Exception) {
                _errorMessage.value = HttpErrorParser.parse(e)
                _isConnectionError.value = HttpErrorParser.isConnectionError(e)
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
        _isConnectionError.value = false
    }
}
