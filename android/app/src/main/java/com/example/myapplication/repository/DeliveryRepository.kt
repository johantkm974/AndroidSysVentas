package com.example.myapplication.repository

import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService

class DeliveryRepository(private val apiService: ApiService) {
    suspend fun listAllEnvios(): Result<List<EnvioResponse>> {
        return try {
            Result.success(apiService.listAllEnvios())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listMyDeliveries(): Result<List<EnvioResponse>> {
        return try {
            Result.success(apiService.myDeliveries())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEnvio(id: Long): Result<EnvioResponse> {
        return try {
            Result.success(apiService.getEnvio(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTracking(id: Long): Result<List<SeguimientoResponse>> {
        return try {
            Result.success(apiService.getTracking(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(id: Long, idEstadoEnvio: Long, observacion: String?): Result<EnvioResponse> {
        return try {
            Result.success(apiService.updateEnvioStatus(id, ActualizarEstadoEnvioRequest(idEstadoEnvio, observacion)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEnvioByPedido(pedidoId: Long): Result<EnvioResponse> {
        return try {
            Result.success(apiService.getEnvioByPedido(pedidoId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignRepartidor(envioId: Long, repartidorId: Long): Result<EnvioResponse> {
        return try {
            Result.success(apiService.assignRepartidor(envioId, repartidorId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
