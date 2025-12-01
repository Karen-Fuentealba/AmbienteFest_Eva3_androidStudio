package com.antaedo_karfuentealba.eva3_ambientefest.data.model

import com.squareup.moshi.Json

data class Servicio(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val provider: String,
    val availability: String?,
    val rating: Double?,
    @Json(name = "num_ratings")
    val num_ratings: Int?,
    val available: Boolean?,
    val status: String?,
    @Json(name = "user_id")
    val user_id: Int?,
    @Json(name = "service_category_id")
    val service_category_id: Int?,
    val imagen: List<XanoImage> = emptyList()
)
