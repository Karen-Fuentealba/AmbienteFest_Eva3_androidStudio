package com.antaedo_karfuentealba.eva3_ambientefest.data.model

import com.squareup.moshi.Json

data class AuthResponse(
    @Json(name = "authToken") val authToken: String?,
    @Json(name = "token") val token: String?,
    val user: User
)

data class User(
    val id: Int,
    val name: String?,
    @Json(name = "last_name") val lastName: String?,
    val email: String,
    @Json(name = "role_id") val roleId: Int?,
    val state: Boolean? = true
)

data class Role(
    val id: Int,
    val name: String,
    val description: String? = null
)

data class CreateUserRequest(
    val name: String,
    val last_name: String? = null,
    val email: String,
    val password: String,
    val role_id: Int,
    val state: Boolean = true
)

data class UpdateUserRequest(
    val name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val role_id: Int? = null,
    val state: Boolean? = null
)

data class UserResponse(
    val id: Int,
    val name: String?,
    @Json(name = "last_name") val lastName: String?,
    val email: String,
    @Json(name = "role_id") val roleId: Int?,
    val state: Boolean? = true,
    val role: Role? = null
)
