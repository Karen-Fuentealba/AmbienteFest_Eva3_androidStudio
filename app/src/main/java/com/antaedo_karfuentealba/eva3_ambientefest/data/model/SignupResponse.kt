package com.antaedo_karfuentealba.eva3_ambientefest.data.model

import com.squareup.moshi.Json

// Signup endpoint in Xano returns only an authToken JSON. Model it exactly.
data class SignupResponse(
    @Json(name = "authToken") val authToken: String?
)

