package com.example.myapplication.repository

import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService

class AuthRepository(private val apiService: ApiService) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<UsuarioResponse> {
        return try {
            val response = apiService.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
