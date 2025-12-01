package com.antaedo_karfuentealba.eva3_ambientefest.data.repository

import android.content.Context
import com.antaedo_karfuentealba.eva3_ambientefest.data.local.SessionDataStore
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.ApiConfig
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.AuthApi


object Graph {
    lateinit var authRepository: AuthRepository
        private set

    fun provide(context: Context) {
        val sessionStore = SessionDataStore(context)
        val authApi = ApiConfig.provideAuthRetrofit().create(AuthApi::class.java)
        authRepository = AuthRepository(authApi, sessionStore)
    }
}