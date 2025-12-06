package com.antaedo_karfuentealba.eva3_ambientefest.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorContenido
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.AuthViewModel
import com.antaedo_karfuentealba.eva3_ambientefest.util.SessionDebugHelper
import kotlinx.coroutines.launch

@Composable
fun AdminHomeScreen(
    navController: NavController? = null,
    authViewModel: AuthViewModel? = null,
    cartViewModel: com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.CartViewModel? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Nombre de la app arriba
        Text(
            "AmbienteFest",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Cursive,
                color = ColorPrincipal
            )
        )

        Spacer(Modifier.height(24.dp))

        // Título Panel Administrador
        Text(
            "Panel Administrador",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Serif,
                color = Color.Black
            )
        )

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mensaje de bienvenida
            Text(
                text = "Bienvenido, Administrador",
                style = MaterialTheme.typography.titleLarge,
                color = ColorContenido
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Panel de control para gestionar servicios y usuarios de AmbienteFest",
                style = MaterialTheme.typography.bodyLarge,
                color = ColorContenido.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(32.dp))

            // Botón principal con ColorPrincipal
            Button(
                onClick = {
                    navController?.navigate("service_management")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrincipal,
                    contentColor = Color.White
                )
            ) {
                Text("Gestionar Servicios")
            }

            Spacer(Modifier.height(8.dp))

            // Botón secundario con ColorPrincipal
            Button(
                onClick = {
                    navController?.navigate("user_management")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrincipal,
                    contentColor = Color.White
                )
            ) {
                Text("Gestionar Usuarios")
            }

            Spacer(Modifier.height(16.dp))

            // TextButton con ColorContenido para cerrar sesión
            TextButton(
                onClick = {
                    // Implementación completa de logout con manejo de corrutinas
                    scope.launch {
                        try {
                            // 1. Limpiar sesión a través del ViewModel
                            authViewModel?.logout()
                            // 1.b Limpiar estado local del carrito si existe
                            cartViewModel?.clearOnLogout()

                            // 2. Limpiar sesión como respaldo adicional y esperar confirmación
                            val sessionCleared = SessionDebugHelper.clearSessionSync(context)

                            // 3. Log para debugging
                            android.util.Log.d("AdminLogout", "Sesión limpiada: $sessionCleared")

                            // 4. Navegar a login limpiando todo el stack de navegación
                            navController?.navigate("login") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("AdminLogout", "Error durante logout: ${e.message}", e)

                            // Aún así navegar a login como fallback
                            navController?.navigate("login") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            ) {
                Text("Cerrar Sesión", color = ColorContenido)
            }
        }
    }
}
