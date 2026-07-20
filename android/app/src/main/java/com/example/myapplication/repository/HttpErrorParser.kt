package com.example.myapplication.repository

import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.io.IOException

object HttpErrorParser {
    fun parse(e: Throwable): String {
        return when (e) {
            is ConnectException, is UnknownHostException -> "No se pudo conectar al servidor. Verifica tu conexión a internet."
            is SocketTimeoutException -> "Tiempo de espera agotado. El servidor no responde."
            is IOException -> "Error de red: ${e.message ?: "Verifica tu conexión"}"
            is HttpException -> parseHttpError(e)
            else -> e.message ?: "Error desconocido"
        }
    }

    private fun parseHttpError(e: HttpException): String {
        val errorBody = try {
            e.response()?.errorBody()?.string()
        } catch (_: Exception) { null }

        if (!errorBody.isNullOrBlank()) {
            try {
                val json = JSONObject(errorBody)
                if (json.has("error")) return json.getString("error")
                if (json.has("message")) return json.getString("message")
                if (json.has("errors")) {
                    val errors = json.getJSONObject("errors")
                    val messages = mutableListOf<String>()
                    errors.keys().forEach { key ->
                        messages.add(errors.getString(key))
                    }
                    if (messages.isNotEmpty()) return messages.joinToString("\n")
                }
            } catch (_: Exception) {}
        }

        return when (e.code()) {
            400 -> "Solicitud incorrecta. Verifica los datos ingresados."
            401 -> "Credenciales inválidas. Correo o contraseña incorrectos."
            403 -> "No tienes permisos para realizar esta acción."
            404 -> "Recurso no encontrado."
            409 -> "Conflicto: el recurso ya existe."
            500 -> "Error interno del servidor. Intenta más tarde."
            502 -> "El servidor no está disponible. Intenta más tarde."
            503 -> "Servicio temporalmente no disponible."
            else -> "Error del servidor (${e.code()})"
        }
    }

    fun isConnectionError(e: Throwable): Boolean {
        return e is ConnectException || e is UnknownHostException || e is SocketTimeoutException || e is IOException
    }
}
