package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.repository.OrderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CartItem(
    val product: ProductoResponse,
    var quantity: Int
)

enum class PaymentMethod(val displayName: String, val icon: String) {
    CREDIT_CARD("Tarjeta de Crédito/Débito", "💳"),
    YAPE_PLIN("Yape/Plin", "📱")
}

class CartViewModel(private val repository: OrderRepository) : ViewModel() {
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

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutState.Idle
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun addToCart(product: ProductoResponse) {
        val currentList = _cartItems.value.toMutableList()
        val existingItem = currentList.find { it.product.idProducto == product.idProducto }
        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            currentList.add(CartItem(product, 1))
        }
        _cartItems.value = currentList
    }

    fun removeFromCart(productId: Long) {
        _cartItems.value = _cartItems.value.filter { it.product.idProducto != productId }
    }

    fun checkout(observacion: String) {
        if (_cartItems.value.isEmpty()) {
            _checkoutState.value = CheckoutState.Error("El carrito está vacío")
            return
        }
        _checkoutState.value = CheckoutState.SimulatingPayment
        val items = _cartItems.value.map { ItemPedido(it.product.idProducto, it.quantity) }
        viewModelScope.launch {
            delay(2500)
            _checkoutState.value = CheckoutState.Loading
            repository.createOrder(PedidoRequest(items, observacion))
                .onSuccess {
                    _cartItems.value = emptyList()
                    _checkoutState.value = CheckoutState.Success
                }
                .onFailure { error ->
                    _checkoutState.value = CheckoutState.Error(error.message ?: "Error al realizar el pedido")
                }
        }
    }
}
