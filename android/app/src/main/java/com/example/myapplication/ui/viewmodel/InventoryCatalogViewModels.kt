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

    fun loadCategorias() {
        viewModelScope.launch {
            try { _categorias.value = apiService.listCategorias() } catch (e: Exception) {}
        }
    }

    fun createCategoria(categoria: Categoria) {
        viewModelScope.launch {
            try { apiService.createCategoria(categoria); loadCategorias() } catch (e: Exception) {}
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
}

// --- Marcas ViewModel ---
class MarcaViewModel(private val apiService: ApiService) : ViewModel() {
    private val _marcas = MutableStateFlow<List<Marca>>(emptyList())
    val marcas: StateFlow<List<Marca>> = _marcas

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError

    fun loadMarcas() {
        viewModelScope.launch {
            try { _marcas.value = apiService.listMarcas() } catch (e: Exception) {}
        }
    }

    fun createMarca(marca: Marca) {
        viewModelScope.launch {
            try { apiService.createMarca(marca); loadMarcas() } catch (e: Exception) {}
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
}

// --- Proveedores ViewModel ---
class ProveedorViewModel(private val apiService: ApiService) : ViewModel() {
    private val _proveedores = MutableStateFlow<List<Proveedor>>(emptyList())
    val proveedores: StateFlow<List<Proveedor>> = _proveedores

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError

    fun loadProveedores() {
        viewModelScope.launch {
            try { _proveedores.value = apiService.listProveedores() } catch (e: Exception) {}
        }
    }

    fun createProveedor(proveedor: Proveedor) {
        viewModelScope.launch {
            try { apiService.createProveedor(proveedor); loadProveedores() } catch (e: Exception) {}
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
}
