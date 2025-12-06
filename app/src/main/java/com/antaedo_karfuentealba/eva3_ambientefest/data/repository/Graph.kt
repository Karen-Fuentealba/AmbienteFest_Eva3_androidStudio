package com.antaedo_karfuentealba.eva3_ambientefest.data.repository

import android.content.Context
import com.antaedo_karfuentealba.eva3_ambientefest.data.local.SessionDataStore
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.ApiConfig
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.AuthApi
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.UserApi
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.RoleApi


object Graph {
    lateinit var authRepository: AuthRepository
        private set

    lateinit var userRepository: UserRepository
        private set

    lateinit var serviceRepository: ServiceRepository
        private set

    fun provide(context: Context) {
        val sessionStore = SessionDataStore(context)

        // AuthApi para autenticación
        val authApi = ApiConfig.provideAuthRetrofit().create(AuthApi::class.java)
        authRepository = AuthRepository(authApi, sessionStore)

        // APIs para gestión de usuarios (usando PROJECT_BASE)
        // Por ahora sin token automático, se puede agregar interceptor después si es necesario
        val projectRetrofit = ApiConfig.provideProjectRetrofit { null }
        val userApi = projectRetrofit.create(UserApi::class.java)
        val roleApi = projectRetrofit.create(RoleApi::class.java)
        userRepository = UserRepository(userApi, roleApi)

        // ServiceRepository - reutilizando la misma instancia que ya existe o creando nueva
        serviceRepository = ServiceRepository()
    }
}