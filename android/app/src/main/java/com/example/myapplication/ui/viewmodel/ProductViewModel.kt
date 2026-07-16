package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Categoria
import com.example.myapplication.model.Marca
import com.example.myapplication.model.ProductoRequest
import com.example.myapplication.model.ProductoResponse
import com.example.myapplication.model.Proveedor
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

    private val _categories = MutableStateFlow<List<Categoria>>(emptyList())
    val categories: StateFlow<List<Categoria>> = _categories

    private val _marcas = MutableStateFlow<List<Marca>>(emptyList())
    val marcas: StateFlow<List<Marca>> = _marcas

    private val _proveedores = MutableStateFlow<List<Proveedor>>(emptyList())
    val proveedores: StateFlow<List<Proveedor>> = _proveedores

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId

    fun loadCategories() {
        viewModelScope.launch {
            repository.listCategories().onSuccess { _categories.value = it }
        }
    }

    fun loadMarcas() {
        viewModelScope.launch {
            repository.listMarcas().onSuccess { _marcas.value = it }
        }
    }

    fun loadProveedores() {
        viewModelScope.launch {
            repository.listProveedores().onSuccess { _proveedores.value = it }
        }
    }

    fun loadProducts() {
        _selectedCategoryId.value = null
        viewModelScope.launch { loadAllProducts() }
    }

    fun loadProductsByCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            if (categoryId == null) {
                loadAllProducts()
            } else {
                repository.listByCategory(categoryId)
                    .onSuccess { products -> _uiState.value = ProductUiState.Success(products) }
                    .onFailure { error -> _uiState.value = ProductUiState.Error(error.message ?: "Error al cargar productos") }
            }
        }
    }

    private suspend fun loadAllProducts() {
        _uiState.value = ProductUiState.Loading
        repository.listProducts()
            .onSuccess { products -> _uiState.value = ProductUiState.Success(products) }
            .onFailure { error -> _uiState.value = ProductUiState.Error(error.message ?: "Error al cargar productos") }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            repository.deleteProduct(id)
                .onSuccess { loadAllProducts() }
        }
    }

    private val _createError = MutableStateFlow<String?>(null)
    val createError: StateFlow<String?> = _createError

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess

    fun createProduct(request: ProductoRequest) {
        _createError.value = null
        _createSuccess.value = false
        viewModelScope.launch {
            repository.createProduct(request)
                .onSuccess {
                    _createSuccess.value = true
                    loadAllProducts()
                }
                .onFailure { error ->
                    _createError.value = error.message ?: "Error al crear producto"
                }
        }
    }

    fun updateProduct(id: Long, request: ProductoRequest) {
        _createError.value = null
        _createSuccess.value = false
        viewModelScope.launch {
            repository.updateProduct(id, request)
                .onSuccess {
                    _createSuccess.value = true
                    loadAllProducts()
                }
                .onFailure { error ->
                    _createError.value = error.message ?: "Error al actualizar producto"
                }
        }
    }

    fun clearCreateState() {
        _createError.value = null
        _createSuccess.value = false
    }

    suspend fun getProductById(id: Long): ProductoResponse? {
        return repository.getProductById(id).getOrNull()
    }
}
