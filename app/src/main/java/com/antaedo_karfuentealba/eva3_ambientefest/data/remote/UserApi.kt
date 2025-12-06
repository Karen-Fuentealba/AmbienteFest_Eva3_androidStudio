package com.antaedo_karfuentealba.eva3_ambientefest.data.remote

import com.antaedo_karfuentealba.eva3_ambientefest.data.model.*
import retrofit2.http.*

interface UserApi {
    @GET("user")
    suspend fun getAllUsers(): List<UserResponse>

    @GET("user/{user_id}")
    suspend fun getUserById(@Path("user_id") userId: Int): UserResponse

    @POST("user")
    suspend fun createUser(@Body request: CreateUserRequest): UserResponse

    @PATCH("user/{user_id}")
    suspend fun updateUser(
        @Path("user_id") userId: Int,
        @Body request: UpdateUserRequest
    ): UserResponse

    @DELETE("user/{user_id}")
    suspend fun deleteUser(@Path("user_id") userId: Int)

    @PUT("user/{user_id}")
    suspend fun replaceUser(
        @Path("user_id") userId: Int,
        @Body request: CreateUserRequest
    ): UserResponse
}
