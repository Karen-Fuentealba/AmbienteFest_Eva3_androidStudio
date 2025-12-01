package com.antaedo_karfuentealba.eva3_ambientefest.data.model.requests

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val role_id: Int = 1,
    val state: Boolean = true
)
