package com.antaedo_karfuentealba.eva3_ambientefest.data.model

// Session - Modelo de dominio para persistencia local de sesión
data class Session(
    val token: String?,
    val userId: Int?,
    val name: String?,
    val email: String?,
    val roleId: Int?
)