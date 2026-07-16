package com.example.myapplication.repository

import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService

class VentaRepository(private val apiService: ApiService) {
    suspend fun listSales(): Result<List<VentaResponse>> {
        return try {
            val response = apiService.listSales()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun processSale(request: VentaRequest): Result<VentaResponse> {
        return try {
            val response = apiService.processSale(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelSale(id: Long): Result<VentaResponse> {
        return try {
            val response = apiService.cancelSale(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(HttpErrorParser.parse(e)))
        }
    }
}
