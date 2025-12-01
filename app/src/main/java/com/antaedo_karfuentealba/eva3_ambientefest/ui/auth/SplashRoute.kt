package com.antaedo_karfuentealba.eva3_ambientefest.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.antaedo_karfuentealba.eva3_ambientefest.data.local.SessionDataStore
import com.antaedo_karfuentealba.eva3_ambientefest.util.SessionDebugHelper
import kotlinx.coroutines.delay

@Composable
fun SplashRoute(
    navController: NavController
) {
    val context = LocalContext.current
    val sessionStore = SessionDataStore(context)
    val session by sessionStore.sessionFlow.collectAsState(initial = null)

    // Mostrar splash screen
    SplashScreen()

    LaunchedEffect(Unit) {
        // Debug: mostrar información de sesión actual (asíncrono, no bloquea UI)
        SessionDebugHelper.printSessionInfo(context)

        // PARA TESTING: comenta/descomenta la siguiente línea para limpiar sesión
        // SessionDebugHelper.clearSession(context)
        // delay(500) // Esperar a que se limpie la sesión

        // Esperar un poco para que el usuario vea la pantalla splash
        delay(1500)

        // Verificar si hay sesión después del delay
        val token = session?.token
        val roleId = session?.roleId

        // Debug: log para verificar qué datos tenemos (usando Log en lugar de println)
        android.util.Log.d("SplashRoute", "Token: $token, RoleId: $roleId")

        if (!token.isNullOrBlank() && token.isNotEmpty()) {
            // Hay sesión válida, navegar según rol
            android.util.Log.d("SplashRoute", "Sesión válida encontrada, navegando...")
            if (roleId == 2) {
                navController.navigate("home_admin") {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                navController.navigate("home_user") {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else {
            // No hay sesión válida, ir a login
            android.util.Log.d("SplashRoute", "No hay sesión, navegando a login")
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
