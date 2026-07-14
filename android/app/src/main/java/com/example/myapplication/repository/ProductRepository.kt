package com.example.myapplication.repository

import com.example.myapplication.model.ProductoRequest
import com.example.myapplication.model.ProductoResponse
import com.example.myapplication.network.ApiService

class ProductRepository(private val apiService: ApiService) {
    suspend fun listProducts(): Result<List<ProductoResponse>> {
        return try {
            val response = apiService.listProducts()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Long): Result<ProductoResponse> {
        return try {
            val response = apiService.getProduct(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(request: ProductoRequest): Result<ProductoResponse> {
        return try {
            val response = apiService.createProduct(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: Long): Result<Unit> {
        return try {
            apiService.deleteProduct(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(id: Long, request: ProductoRequest): Result<ProductoResponse> {
        return try {
            val response = apiService.updateProduct(id, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
