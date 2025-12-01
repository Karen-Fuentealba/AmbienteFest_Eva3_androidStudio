package com.antaedo_karfuentealba.eva3_ambientefest.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiConfig {
    private const val AUTH_BASE = "https://x8ki-letl-twmt.n7.xano.io/api:KBcldO_7/"
    private const val PROJECT_BASE = "https://x8ki-letl-twmt.n7.xano.io/api:OdHOEeXs/"

    // Configuración de Moshi para usar reflexión. Es más robusto ante problemas de build.
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Ahora aceptamos un tokenProvider opcional para que las llamadas a AUTH_BASE puedan incluir Authorization.
    fun provideAuthRetrofit(tokenProvider: () -> String? = { null }): Retrofit {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val authInterceptor = Interceptor { chain ->
            val req = chain.request().newBuilder().apply {
                tokenProvider()?.let { addHeader("Authorization", "Bearer $it") }
            }.build()
            chain.proceed(req)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(AUTH_BASE)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    fun provideProjectRetrofit(tokenProvider: () -> String?): Retrofit {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val authInterceptor = Interceptor { chain ->
            val req = chain.request().newBuilder().apply {
                tokenProvider()?.let { addHeader("Authorization", "Bearer $it") }
            }.build()
            chain.proceed(req)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(PROJECT_BASE)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }
}