package com.antaedo_karfuentealba.eva3_ambientefest.data.repository

import com.antaedo_karfuentealba.eva3_ambientefest.data.model.*
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.RoleApi
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.UserApi

class UserRepository(
    private val userApi: UserApi,
    private val roleApi: RoleApi
) {
    suspend fun getAllUsers(): Result<List<UserResponse>> {
        return try {
            val users = userApi.getAllUsers()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: Int): Result<UserResponse> {
        return try {
            val user = userApi.getUserById(userId)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUser(
        name: String,
        lastName: String?,
        email: String,
        password: String,
        roleId: Int
    ): Result<UserResponse> {
        return try {
            val request = CreateUserRequest(
                name = name,
                last_name = lastName,
                email = email,
                password = password,
                role_id = roleId,
                state = true
            )
            val user = userApi.createUser(request)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(
        userId: Int,
        name: String? = null,
        lastName: String? = null,
        email: String? = null,
        roleId: Int? = null
    ): Result<UserResponse> {
        return try {
            val request = UpdateUserRequest(
                name = name,
                last_name = lastName,
                email = email,
                role_id = roleId
            )
            val user = userApi.updateUser(userId, request)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: Int): Result<Unit> {
        return try {
            userApi.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllRoles(): Result<List<Role>> {
        return try {
            val roles = roleApi.getAllRoles()
            Result.success(roles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
