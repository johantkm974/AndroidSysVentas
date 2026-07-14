package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.ProductoRequest
import com.example.myapplication.model.ProductoResponse
import com.example.myapplication.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProductUiState {
    object Loading : ProductUiState()
    data class Success(val products: List<ProductoResponse>) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            repository.listProducts()
                .onSuccess { products ->
                    _uiState.value = ProductUiState.Success(products)
                }
                .onFailure { error ->
                    _uiState.value = ProductUiState.Error(error.message ?: "Error al cargar productos")
                }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            repository.deleteProduct(id)
                .onSuccess { loadProducts() }
                .onFailure { /* Manejar error */ }
        }
    }

    fun createProduct(request: ProductoRequest) {
        viewModelScope.launch {
            repository.createProduct(request)
                .onSuccess { loadProducts() }
                .onFailure { /* Manejar error */ }
        }
    }

    fun updateProduct(id: Long, request: ProductoRequest) {
        viewModelScope.launch {
            repository.updateProduct(id, request)
                .onSuccess { loadProducts() }
                .onFailure { /* Manejar error */ }
        }
    }

    suspend fun getProductById(id: Long): ProductoResponse? {
        return repository.getProductById(id).getOrNull()
    }
}
