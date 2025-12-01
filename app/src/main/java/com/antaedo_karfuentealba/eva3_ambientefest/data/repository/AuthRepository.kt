package com.antaedo_karfuentealba.eva3_ambientefest.data.repository


import android.util.Log
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.AuthApi
import com.antaedo_karfuentealba.eva3_ambientefest.data.local.SessionDataStore
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.User
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Session
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.AuthResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.SignupResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.requests.LoginRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.requests.SignupRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.ApiConfig
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class AuthRepository(
    private val api: AuthApi,
    private val sessionStore: SessionDataStore
) {
    private val TAG = "AuthRepository"

    private fun extractToken(resp: AuthResponse): String? {
        return resp.authToken ?: resp.token
    }

    // Login contra Xano y guardar sesión - AHORA USA DTOs
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = LoginRequest(email, password)
            Log.d(TAG, "login request: $request")
            Log.d(TAG, "Attempting login for email: $email")

            val response = api.login(request)
            val token = extractToken(response)
            Log.d(TAG, "login token (debug): $token")
            val user = response.user
            sessionStore.saveSession(token, user.id, user.email, user.roleId, user.name)

            Log.d(TAG, "Login successful for user: ${user.email}")
            Result.success(user)
        } catch (e: HttpException) {
            val errorCode = e.code()
            val errorBody = e.response()?.errorBody()?.string()

            Log.e(TAG, "HTTP $errorCode error: $errorBody")

            val userFriendlyMessage = when (errorCode) {
                403 -> "Credenciales incorrectas. Verifica tu email y contraseña."
                404 -> "Servicio no disponible. Intenta más tarde."
                500 -> "Error del servidor. Intenta más tarde."
                else -> "Error de conexión ($errorCode). Verifica tu internet."
            }

            Log.e(TAG, "login error: $errorCode - $errorBody", e)
            Result.failure(Exception(userFriendlyMessage))
        } catch (e: Exception) {
            Log.e(TAG, "login exception", e)
            Result.failure(Exception("Error de conexión. Verifica tu internet."))
        }
    }

    // Registro contra Xano y guardar sesión - AHORA USA DTOs
    suspend fun signup(name: String, lastName: String, email: String, password: String): Result<User> {
        // normalize inputs per Xano expectations
        val normalizedEmail = email.trim().lowercase()
        val normalizedPassword = password // do not hash
        val signupRequest = SignupRequest(
            name = name,
            last_name = lastName,
            email = normalizedEmail,
            password = normalizedPassword
        )

        try {
            Log.d(TAG, "signup request: $signupRequest")
            val signupResponse: SignupResponse = api.signup(signupRequest)
            val signupToken = signupResponse.authToken
            Log.d(TAG, "signup token (debug): $signupToken")

            var finalToken: String? = signupToken
            var user: User? = null

            // Try to login to obtain full auth (token + user)
            try {
                val loginResponse = api.login(LoginRequest(normalizedEmail, normalizedPassword))
                val loginToken = extractToken(loginResponse)
                finalToken = loginToken ?: finalToken
                user = loginResponse.user
                Log.d(TAG, "post-signup login succeeded, user=${user.email}")
            } catch (ex: Exception) {
                Log.e(TAG, "post-signup login failed", ex)
            }

            // If login didn't return user but signup provided token, attempt to call /auth/me using that token
            if (user == null && !signupToken.isNullOrBlank()) {
                try {
                    val tempApi = ApiConfig.provideAuthRetrofit(tokenProvider = { signupToken }).create(AuthApi::class.java)
                    val meUser = tempApi.me()
                    user = meUser
                    finalToken = signupToken
                    Log.d(TAG, "me() with signupToken succeeded, user=${user.email}")
                } catch (ex: Exception) {
                    Log.e(TAG, "me() with signupToken failed", ex)
                }
            }

            if (user == null) {
                // Do not save an incomplete session; return a helpful message
                return Result.failure(Exception("Registro completado pero no se pudo obtener el usuario. Inicia sesión con tus credenciales."))
            }

            sessionStore.saveSession(finalToken, user.id, user.email, user.roleId, user.name)
            return Result.success(user)
        } catch (e: HttpException) {
            val errorCode = e.code()
            val errorBody = e.response()?.errorBody()?.string()
            Log.e(TAG, "signup HTTP $errorCode errorBody: $errorBody")


            val msg = parseHttpErrorWithBody(e)
            Log.e(TAG, "signup error: $msg", e)
            return Result.failure(Exception(msg))
        } catch (e: Exception) {
            Log.e(TAG, "signup exception", e)
            return Result.failure(e)
        }
    }

    // Flujo de sesión (para saber si hay usuario logueado)
    fun session(): Flow<Session> = sessionStore.sessionFlow

    // Logout (limpia DataStore)
    suspend fun logout() = sessionStore.clearSession()

    private fun parseHttpError(e: HttpException): String {
        return when (e.code()) {
            400 -> "Datos inválidos"
            401 -> "Correo o contraseña incorrectos"
            403 -> "Acceso denegado"
            409 -> "El correo ya está registrado"
            500 -> "Error del servidor"
            else -> "Error: ${e.message()}"
        }
    }

    private fun parseHttpErrorWithBody(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e(TAG, "HTTP ${e.code()} errorBody: $errorBody")
            if (!errorBody.isNullOrBlank()) {
                "${e.code()} - ${errorBody}"
            } else {
                parseHttpError(e)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "parseHttpErrorWithBody exception", ex)
            parseHttpError(e)
        }
    }
}