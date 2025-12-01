package com.antaedo_karfuentealba.eva3_ambientefest.data.remote

import com.antaedo_karfuentealba.eva3_ambientefest.data.model.AuthResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.SignupResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.User
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.requests.LoginRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.requests.SignupRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    // Xano auth endpoints live under the "auth/" path for signup and me; login lives at /login
    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // Signup returns only { "authToken": "..." } on success
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): SignupResponse

    @GET("auth/me")
    suspend fun me(): User
}