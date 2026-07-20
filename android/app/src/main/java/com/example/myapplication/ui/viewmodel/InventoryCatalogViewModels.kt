package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.HttpErrorParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// --- Categorías ViewModel ---
class CategoriaViewModel(private val apiService: ApiService) : ViewModel() {
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isConnectionError = MutableStateFlow(false)
    val isConnectionError: StateFlow<Boolean> = _isConnectionError

    fun loadCategorias() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isConnectionError.value = false
            try {
                _categorias.value = apiService.listCategorias()
            } catch (e: Exception) {
                _errorMessage.value = HttpErrorParser.parse(e)
                _isConnectionError.value = HttpErrorParser.isConnectionError(e)
            }
            _isLoading.value = false
        }
    }

    fun createCategoria(categoria: Categoria) {
        viewModelScope.launch {
            try {
                apiService.createCategoria(categoria)
                loadCategorias()
            } catch (e: Exception) {
                _deleteError.value = HttpErrorParser.parse(e)
            }
        }
    }

    fun deleteCategoria(id: Long) {
        viewModelScope.launch {
            try {
                apiService.deleteCategoria(id)
                loadCategorias()
                _deleteError.value = null
            } catch (e: Exception) {
                _deleteError.value = HttpErrorParser.parse(e)
            }
        }
    }

    fun clearDeleteError() {
        _deleteError.value = null
    }

    fun clearError() {
        _errorMessage.value = null
        _isConnectionError.value = false
    }
}

// --- Marcas ViewModel ---
class MarcaViewModel(private val apiService: ApiService) : ViewModel() {
    private val _marcas = MutableStateFlow<List<Marca>>(emptyList())
    val marcas: StateFlow<List<Marca>> = _marcas

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isConnectionError = MutableStateFlow(false)
    val isConnectionError: StateFlow<Boolean> = _isConnectionError

    fun loadMarcas() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isConnectionError.value = false
            try {
                _marcas.value = apiService.listMarcas()
            } catch (e: Exception) {
                _errorMessage.value = HttpErrorParser.parse(e)
                _isConnectionError.value = HttpErrorParser.isConnectionError(e)
            }
            _isLoading.value = false
        }
    }

    fun createMarca(marca: Marca) {
        viewModelScope.launch {
            try {
                apiService.createMarca(marca)
                loadMarcas()
            } catch (e: Exception) {
                _deleteError.value = HttpErrorParser.parse(e)
            }
        }
    }

    fun deleteMarca(id: Long) {
        viewModelScope.launch {
            try {
                apiService.deleteMarca(id)
                loadMarcas()
                _deleteError.value = null
            } catch (e: Exception) {
                _deleteError.value = HttpErrorParser.parse(e)
            }
        }
    }

    fun clearDeleteError() {
        _deleteError.value = null
    }

    fun clearError() {
        _errorMessage.value = null
        _isConnectionError.value = false
    }
}

// --- Proveedores ViewModel ---
class ProveedorViewModel(private val apiService: ApiService) : ViewModel() {
    private val _proveedores = MutableStateFlow<List<Proveedor>>(emptyList())
    val proveedores: StateFlow<List<Proveedor>> = _proveedores

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isConnectionError = MutableStateFlow(false)
    val isConnectionError: StateFlow<Boolean> = _isConnectionError

    fun loadProveedores() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isConnectionError.value = false
            try {
                _proveedores.value = apiService.listProveedores()
            } catch (e: Exception) {
                _errorMessage.value = HttpErrorParser.parse(e)
                _isConnectionError.value = HttpErrorParser.isConnectionError(e)
            }
            _isLoading.value = false
        }
    }

    fun createProveedor(proveedor: Proveedor) {
        viewModelScope.launch {
            try {
                apiService.createProveedor(proveedor)
                loadProveedores()
            } catch (e: Exception) {
                _deleteError.value = HttpErrorParser.parse(e)
            }
        }
    }

    fun deleteProveedor(id: Long) {
        viewModelScope.launch {
            try {
                apiService.deleteProveedor(id)
                loadProveedores()
                _deleteError.value = null
            } catch (e: Exception) {
                _deleteError.value = HttpErrorParser.parse(e)
            }
        }
    }

    fun clearDeleteError() {
        _deleteError.value = null
    }

    fun clearError() {
        _errorMessage.value = null
        _isConnectionError.value = false
    }
}
