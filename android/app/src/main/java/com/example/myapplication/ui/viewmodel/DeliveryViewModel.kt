package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.repository.DeliveryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeliveryViewModel(private val repository: DeliveryRepository) : ViewModel() {
    private val _deliveries = MutableStateFlow<List<EnvioResponse>>(emptyList())
    val deliveries: StateFlow<List<EnvioResponse>> = _deliveries

    private val _allEnvios = MutableStateFlow<List<EnvioResponse>>(emptyList())
    val allEnvios: StateFlow<List<EnvioResponse>> = _allEnvios

    private val _tracking = MutableStateFlow<List<SeguimientoResponse>>(emptyList())
    val tracking: StateFlow<List<SeguimientoResponse>> = _tracking

    private val _selectedEnvio = MutableStateFlow<EnvioResponse?>(null)
    val selectedEnvio: StateFlow<EnvioResponse?> = _selectedEnvio

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadMyDeliveries() {
        viewModelScope.launch {
            repository.listMyDeliveries().onSuccess { _deliveries.value = it }
        }
    }

    fun loadAllEnvios() {
        viewModelScope.launch {
            repository.listAllEnvios().onSuccess { _allEnvios.value = it }
        }
    }

    fun loadTracking(idEnvio: Long) {
        viewModelScope.launch {
            repository.getTracking(idEnvio).onSuccess { _tracking.value = it }
        }
    }

    fun loadEnvio(idEnvio: Long) {
        viewModelScope.launch {
            repository.getEnvio(idEnvio).onSuccess { _selectedEnvio.value = it }
        }
    }

    fun updateToInRoute(idEnvio: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(5000)
            repository.updateStatus(idEnvio, 2, "Repartidor en camino").onSuccess {
                loadEnvio(idEnvio)
                loadTracking(idEnvio)
                loadMyDeliveries()
                loadAllEnvios()
            }
            _isLoading.value = false
        }
    }

    fun updateToDelivered(idEnvio: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(5000)
            repository.updateStatus(idEnvio, 3, "Paquete entregado").onSuccess {
                loadEnvio(idEnvio)
                loadTracking(idEnvio)
                loadMyDeliveries()
                loadAllEnvios()
            }
            _isLoading.value = false
        }
    }

    fun cancelEnvio(idEnvio: Long) {
        viewModelScope.launch {
            repository.updateStatus(idEnvio, 4, "Envío cancelado").onSuccess {
                loadAllEnvios()
            }
        }
    }
}
