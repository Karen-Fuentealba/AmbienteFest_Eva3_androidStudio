package com.antaedo_karfuentealba.eva3_ambientefest.util

import android.content.Context
import android.util.Log
import com.antaedo_karfuentealba.eva3_ambientefest.data.local.SessionDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Utility object para debugging y testing de sesiones
 */
object SessionDebugHelper {

    /**
     * Limpia completamente la sesión guardada en DataStore
     * Útil para testing y debugging
     */
    fun clearSession(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessionStore = SessionDataStore(context)
                sessionStore.clearSession()
                Log.d("SessionDebugHelper", "Sesión limpiada completamente")

                // Verificar que realmente se limpió
                val sessionAfterClear = sessionStore.sessionFlow.first()
                if (sessionAfterClear?.token.isNullOrBlank()) {
                    Log.d("SessionDebugHelper", "✓ Verificación: Sesión efectivamente limpia")
                } else {
                    Log.w("SessionDebugHelper", "⚠ Advertencia: Aún hay datos de sesión después del clear")
                }
            } catch (e: Exception) {
                Log.e("SessionDebugHelper", "Error limpiando sesión: ${e.message}", e)
            }
        }
    }

    /**
     * Muestra información de la sesión actual de forma asíncrona
     */
    fun printSessionInfo(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessionStore = SessionDataStore(context)
                val session = sessionStore.sessionFlow.first()
                Log.d("SessionDebugHelper", "Token: ${session?.token}")
                Log.d("SessionDebugHelper", "UserId: ${session?.userId}")
                Log.d("SessionDebugHelper", "Email: ${session?.email}")
                Log.d("SessionDebugHelper", "RoleId: ${session?.roleId}")
                Log.d("SessionDebugHelper", "Name: ${session?.name}")
            } catch (e: Exception) {
                Log.e("SessionDebugHelper", "Error obteniendo información de sesión: ${e.message}")
            }
        }
    }

    /**
     * Versión síncrona de clear session para casos donde necesitamos esperar
     * USAR SOLO en coroutines
     */
    suspend fun clearSessionSync(context: Context): Boolean {
        return try {
            val sessionStore = SessionDataStore(context)
            sessionStore.clearSession()
            Log.d("SessionDebugHelper", "Sesión limpiada completamente (sync)")

            // Verificar que realmente se limpió
            val sessionAfterClear = sessionStore.sessionFlow.first()
            val isClean = sessionAfterClear?.token.isNullOrBlank()
            if (isClean) {
                Log.d("SessionDebugHelper", "✓ Verificación: Sesión efectivamente limpia")
            } else {
                Log.w("SessionDebugHelper", "⚠ Advertencia: Aún hay datos de sesión después del clear")
            }
            isClean
        } catch (e: Exception) {
            Log.e("SessionDebugHelper", "Error limpiando sesión: ${e.message}", e)
            false
        }
    }

    /**
     * Versión síncrona para casos específicos donde se necesita el resultado inmediatamente
     * USAR CON CUIDADO - Solo en coroutines
     */
    suspend fun getSessionInfo(context: Context): com.antaedo_karfuentealba.eva3_ambientefest.data.model.Session? {
        return try {
            val sessionStore = SessionDataStore(context)
            sessionStore.sessionFlow.first()
        } catch (e: Exception) {
            Log.e("SessionDebugHelper", "Error obteniendo información de sesión: ${e.message}")
            null
        }
    }
}
