package com.example.myapplication.repository

import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService

class UserRepository(private val apiService: ApiService) {
    suspend fun listUsers(): Result<List<UsuarioResponse>> {
        return try {
            val response = apiService.listUsers()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Long): Result<UsuarioResponse> {
        return try {
            val response = apiService.getUser(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUser(request: RegisterRequest): Result<UsuarioResponse> {
        return try {
            val response = apiService.createUser(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(id: Long, request: UpdateUserRequest): Result<UsuarioResponse> {
        return try {
            val response = apiService.updateUser(id, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(id: Long): Result<Unit> {
        return try {
            apiService.deleteUser(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listRoles(): Result<List<RoleResponse>> {
        return try {
            val response = apiService.listRoles()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<UsuarioResponse> {
        return try {
            val response = apiService.perfil()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
