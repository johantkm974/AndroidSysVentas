package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.datastore.SessionManager
import com.example.myapplication.model.LoginRequest
import com.example.myapplication.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val roles: List<String>) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    fun login(correo: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            repository.login(LoginRequest(correo, pass))
                .onSuccess { response ->
                    sessionManager.saveSession(
                        token = response.token,
                        correo = response.correo,
                        nombres = response.nombres,
                        userId = response.idUsuario,
                        roles = response.roles
                    )
                    _uiState.value = LoginUiState.Success(response.roles)
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Error(error.message ?: "Error desconocido")
                }
        }
    }
}
