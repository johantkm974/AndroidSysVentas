package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CartItem(
    val product: ProductoResponse,
    var quantity: Int
)

class CartViewModel(private val repository: OrderRepository) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

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

    fun checkout(observacion: String, onSuccess: () -> Unit) {
        val items = _cartItems.value.map { ItemPedido(it.product.idProducto, it.quantity) }
        viewModelScope.launch {
            repository.createOrder(PedidoRequest(items, observacion)).onSuccess {
                _cartItems.value = emptyList()
                onSuccess()
            }
        }
    }
}
