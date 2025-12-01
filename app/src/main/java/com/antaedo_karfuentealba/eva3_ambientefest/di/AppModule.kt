package com.antaedo_karfuentealba.eva3_ambientefest.di

// di/AppModule.kt
import android.content.Context
import com.antaedo_karfuentealba.eva3_ambientefest.data.local.SessionDataStore
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.ApiConfig
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.AuthApi
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.AuthRepository
import retrofit2.create
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class AppModule(context: Context) {
    private val sessionStore = SessionDataStore(context)

    // Token provider que lee el token almacenado de forma bloqueante.
    private val tokenProvider: () -> String? = {
        runBlocking { sessionStore.sessionFlow.first().token }
    }

    // Crear retrofit para AUTH_BASE (añade Authorization si hay token).
    private val authRetrofit = ApiConfig.provideAuthRetrofit(tokenProvider)
    private val authApi: AuthApi = authRetrofit.create()

    val authRepository = AuthRepository(
        api = authApi,
        sessionStore = sessionStore
    )

    // Token provider para llamadas autenticadas
    // (nota: el tokenProvider ya está definido y usado arriba)
}