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
