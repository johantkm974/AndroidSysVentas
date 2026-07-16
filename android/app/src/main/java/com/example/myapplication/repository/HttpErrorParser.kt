package com.example.myapplication.repository

import org.json.JSONObject
import retrofit2.HttpException

object HttpErrorParser {
    fun parse(e: Exception): String {
        if (e is HttpException) {
            try {
                e.response()?.errorBody()?.string()?.let { body ->
                    val json = JSONObject(body)
                    if (json.has("error")) return json.getString("error")
                    if (json.has("message")) return json.getString("message")
                }
            } catch (_: Exception) {}
            return "Error del servidor (${e.code()})"
        }
        return e.message ?: "Error desconocido"
    }
}
