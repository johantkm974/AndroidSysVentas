package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.repository.OrderRepository
import com.example.myapplication.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CartItem(
    val product: ProductoResponse,
    val quantity: Int
)

enum class PaymentMethod(val displayName: String, val icon: String) {
    CREDIT_CARD("Tarjeta de Crédito/Débito", "💳"),
    YAPE_PLIN("Yape/Plin", "📱")
}

class CartViewModel(
    private val repository: OrderRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.YAPE_PLIN)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod

    sealed class CheckoutState {
        object Idle : CheckoutState()
        object SimulatingPayment : CheckoutState()
        object Loading : CheckoutState()
        object Success : CheckoutState()
        data class Error(val message: String) : CheckoutState()
    }

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion

    private val _distrito = MutableStateFlow("")
    val distrito: StateFlow<String> = _distrito

    fun loadUserProfile() {
        viewModelScope.launch {
            userRepository.getProfile().onSuccess { user ->
                if (_direccion.value.isBlank() && !user.direccion.isNullOrBlank()) {
                    _direccion.value = user.direccion
                }
            }
        }
    }

    fun updateDireccion(value: String) {
        _direccion.value = value
        if (_checkoutState.value is CheckoutState.Error) _checkoutState.value = CheckoutState.Idle
    }

    fun updateDistrito(value: String) {
        _distrito.value = value
        if (_checkoutState.value is CheckoutState.Error) _checkoutState.value = CheckoutState.Idle
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutState.Idle
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun addToCart(product: ProductoResponse) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.idProducto == product.idProducto }
        val currentQty = if (index >= 0) currentList[index].quantity else 0
        if (currentQty >= product.stock) return
        if (index >= 0) {
            currentList[index] = currentList[index].copy(quantity = currentQty + 1)
        } else {
            currentList.add(CartItem(product, 1))
        }
        _cartItems.value = currentList
    }

    fun increaseQuantity(productId: Long) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.idProducto == productId }
        if (index >= 0) {
            val item = currentList[index]
            if (item.quantity >= item.product.stock) return
            currentList[index] = item.copy(quantity = item.quantity + 1)
            _cartItems.value = currentList
        }
    }

    fun decreaseQuantity(productId: Long) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.idProducto == productId }
        if (index >= 0) {
            val newQty = currentList[index].quantity - 1
            if (newQty <= 0) {
                _cartItems.value = currentList.filter { it.product.idProducto != productId }
            } else {
                currentList[index] = currentList[index].copy(quantity = newQty)
                _cartItems.value = currentList
            }
        }
    }

    fun removeFromCart(productId: Long) {
        _cartItems.value = _cartItems.value.filter { it.product.idProducto != productId }
    }

    fun checkout(observacion: String) {
        _checkoutState.value = CheckoutState.Idle
        if (_cartItems.value.isEmpty()) {
            _checkoutState.value = CheckoutState.Error("El carrito está vacío")
            return
        }
        if (_direccion.value.isBlank() || _distrito.value.isBlank()) {
            _checkoutState.value = CheckoutState.Error("Debes ingresar dirección y distrito de envío")
            return
        }
        _checkoutState.value = CheckoutState.SimulatingPayment
        val items = _cartItems.value.map { ItemPedido(it.product.idProducto, it.quantity) }
        viewModelScope.launch {
            delay(2500)
            _checkoutState.value = CheckoutState.Loading
            repository.createOrder(
                PedidoRequest(
                    items = items,
                    observacion = observacion.ifBlank { null },
                    direccion = _direccion.value,
                    distrito = _distrito.value
                )
            )
                .onSuccess {
                    _cartItems.value = emptyList()
                    _direccion.value = ""
                    _distrito.value = ""
                    _checkoutState.value = CheckoutState.Success
                }
                .onFailure { error ->
                    _checkoutState.value = CheckoutState.Error(error.message ?: "Error al realizar el pedido")
                }
        }
    }
}
