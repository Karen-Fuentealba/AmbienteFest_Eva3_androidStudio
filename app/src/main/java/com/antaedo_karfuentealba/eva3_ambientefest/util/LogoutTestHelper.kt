package com.antaedo_karfuentealba.eva3_ambientefest.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utility para testing del logout del administrador
 */
object LogoutTestHelper {

    /**
     * Test del flujo completo de logout
     */
    fun testLogout(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("LogoutTest", "=== INICIANDO TEST DE LOGOUT ===")

                // 1. Verificar sesión antes del logout
                val sessionBefore = SessionDebugHelper.getSessionInfo(context)
                Log.d("LogoutTest", "Sesión antes del logout:")
                Log.d("LogoutTest", "  - Token: ${sessionBefore?.token}")
                Log.d("LogoutTest", "  - UserId: ${sessionBefore?.userId}")
                Log.d("LogoutTest", "  - Email: ${sessionBefore?.email}")
                Log.d("LogoutTest", "  - RoleId: ${sessionBefore?.roleId}")

                // 2. Ejecutar logout
                val logoutSuccess = SessionDebugHelper.clearSessionSync(context)
                Log.d("LogoutTest", "Logout ejecutado: $logoutSuccess")

                // 3. Verificar sesión después del logout
                val sessionAfter = SessionDebugHelper.getSessionInfo(context)
                Log.d("LogoutTest", "Sesión después del logout:")
                Log.d("LogoutTest", "  - Token: ${sessionAfter?.token}")
                Log.d("LogoutTest", "  - UserId: ${sessionAfter?.userId}")
                Log.d("LogoutTest", "  - Email: ${sessionAfter?.email}")
                Log.d("LogoutTest", "  - RoleId: ${sessionAfter?.roleId}")

                // 4. Verificar que la sesión está limpia
                val isClean = sessionAfter?.token.isNullOrBlank()
                Log.d("LogoutTest", "¿Sesión limpia? $isClean")

                if (isClean) {
                    Log.d("LogoutTest", "✅ TEST EXITOSO: Logout funcionó correctamente")
                } else {
                    Log.e("LogoutTest", "❌ TEST FALLIDO: Logout no limpió la sesión")
                }

                Log.d("LogoutTest", "=== FIN DEL TEST DE LOGOUT ===")

            } catch (e: Exception) {
                Log.e("LogoutTest", "Error en test de logout: ${e.message}", e)
            }
        }
    }

    /**
     * Simula una sesión para hacer testing
     */
    fun createTestSession(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessionStore = com.antaedo_karfuentealba.eva3_ambientefest.data.local.SessionDataStore(context)
                sessionStore.saveSession(
                    token = "test_token_12345",
                    userId = 999,
                    email = "admin@test.com",
                    roleId = 2,
                    name = "Test Admin"
                )
                Log.d("LogoutTest", "✅ Sesión de prueba creada")
            } catch (e: Exception) {
                Log.e("LogoutTest", "Error creando sesión de prueba: ${e.message}", e)
            }
        }
    }

    /**
     * Verifica el estado actual de la sesión
     */
    fun checkCurrentSession(context: Context) {
        SessionDebugHelper.printSessionInfo(context)
    }
}
