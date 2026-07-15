package com.example.myapplication.repository

import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService
import retrofit2.HttpException

class AuthRepository(private val apiService: ApiService) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(request)
            Result.success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                Result.failure(Exception("Usuario o contraseña incorrectas"))
            } else {
                Result.failure(Exception("Error del servidor: ${e.message()}"))
            }
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
