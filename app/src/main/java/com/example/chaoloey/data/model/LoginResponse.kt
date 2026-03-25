package com.example.chaoloey.data.model

data class LoginResponse(
    val success: Boolean,
    val data: LoginData?,
    val message: String? = null,
    val code: String? = null
)

data class LoginData(
    val user: User?,
    val token: String?
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String
)

data class ErrorResponse(
    val success: Boolean,
    val code: String? = null,
    val message: String? = null
)
