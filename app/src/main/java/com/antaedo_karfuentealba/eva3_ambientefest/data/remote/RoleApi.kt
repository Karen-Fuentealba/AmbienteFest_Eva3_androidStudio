package com.antaedo_karfuentealba.eva3_ambientefest.data.remote

import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Role
import retrofit2.http.*

interface RoleApi {
    @GET("role")
    suspend fun getAllRoles(): List<Role>

    @GET("role/{role_id}")
    suspend fun getRoleById(@Path("role_id") roleId: Int): Role

    @POST("role")
    suspend fun createRole(@Body request: CreateRoleRequest): Role

    @PATCH("role/{role_id}")
    suspend fun updateRole(
        @Path("role_id") roleId: Int,
        @Body request: UpdateRoleRequest
    ): Role

    @DELETE("role/{role_id}")
    suspend fun deleteRole(@Path("role_id") roleId: Int)
}

data class CreateRoleRequest(
    val name: String,
    val description: String? = null
)

data class UpdateRoleRequest(
    val name: String? = null,
    val description: String? = null
)
