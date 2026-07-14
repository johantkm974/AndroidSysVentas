package com.example.myapplication.repository

import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService

class OrderRepository(private val apiService: ApiService) {
    suspend fun listAllOrders(): Result<List<PedidoResponse>> {
        return try {
            val response = apiService.listOrders()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listMyOrders(): Result<List<PedidoResponse>> {
        return try {
            val response = apiService.myOrders()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrder(request: PedidoRequest): Result<PedidoResponse> {
        return try {
            val response = apiService.createOrder(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderById(id: Long): Result<PedidoResponse> {
        return try {
            val response = apiService.getOrder(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(id: Long, idEstado: Long): Result<PedidoResponse> {
        return try {
            val response = apiService.updateOrderStatus(id, idEstado)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelOrder(id: Long): Result<PedidoResponse> {
        return try {
            val response = apiService.cancelOrder(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
