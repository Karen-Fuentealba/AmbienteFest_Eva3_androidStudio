package com.antaedo_karfuentealba.eva3_ambientefest.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TimeSlot(
    val id: Int,
    val service_id: Int,
    val date: String,
    val start_time: String,
    val end_time: String
)

