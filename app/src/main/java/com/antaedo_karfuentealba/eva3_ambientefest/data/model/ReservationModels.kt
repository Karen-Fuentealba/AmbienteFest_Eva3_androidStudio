package com.antaedo_karfuentealba.eva3_ambientefest.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReservationRequest(
    val service_id: Int,
    val user_id: Int,
    val time_slot_id: Int,
    val status: String = "pending",
    val notes: String = "",
    val confirmed_at: String = ""
)

@JsonClass(generateAdapter = true)
data class ReservationResponse(
    val id: Int,
    val service_id: Int,
    val user_id: Int,
    val time_slot_id: Int,
    val status: String
)
