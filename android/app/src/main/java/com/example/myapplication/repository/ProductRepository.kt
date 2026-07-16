package com.example.myapplication.repository

import com.example.myapplication.model.Marca
import com.example.myapplication.model.ProductoRequest
import com.example.myapplication.model.ProductoResponse
import com.example.myapplication.model.Proveedor
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

    suspend fun listByCategory(idCategoria: Long): Result<List<ProductoResponse>> {
        return try {
            val response = apiService.listProductsByCategory(idCategoria)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listMarcas(): Result<List<Marca>> {
        return try {
            val response = apiService.listMarcas()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listProveedores(): Result<List<Proveedor>> {
        return try {
            val response = apiService.listProveedores()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listCategories(): Result<List<com.example.myapplication.model.Categoria>> {
        return try {
            val response = apiService.listCategorias()
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
            Result.failure(Exception(HttpErrorParser.parse(e)))
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
            Result.failure(Exception(HttpErrorParser.parse(e)))
        }
    }
}
