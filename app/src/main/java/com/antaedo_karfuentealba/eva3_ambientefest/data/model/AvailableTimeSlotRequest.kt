package com.antaedo_karfuentealba.eva3_ambientefest.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AvailableTimeSlotRequest(
    val service_id: Int,
    val date: String = "",
    val start_time: String = "",
    val end_time: String = "",
    val created_by_: Int = 0,
    val is_booked: Boolean = false
)

