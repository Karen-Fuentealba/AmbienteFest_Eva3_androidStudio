package com.antaedo_karfuentealba.eva3_ambientefest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.antaedo_karfuentealba.eva3_ambientefest.ui.auth.LoginRoute
import com.antaedo_karfuentealba.eva3_ambientefest.ui.auth.SignupRoute
import com.antaedo_karfuentealba.eva3_ambientefest.ui.auth.SplashRoute
import com.antaedo_karfuentealba.eva3_ambientefest.ui.services.ServicesRoute
import com.antaedo_karfuentealba.eva3_ambientefest.ui.admin.AdminHomeScreen
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.AuthViewModel
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.CartViewModel
import com.antaedo_karfuentealba.eva3_ambientefest.ui.cart.CartRoute
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.antaedo_karfuentealba.eva3_ambientefest.util.SessionDebugHelper
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel? = null
) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            // Splash checks session and navigates accordingly
            SplashRoute(navController = navController)
        }

        composable("login") {
            LoginRoute(
                viewModel = authViewModel,
                onLoginSuccess = { _roleId ->
                    // Leer userId desde el estado del AuthViewModel y cargar carrito para ese usuario
                    val userId = authViewModel.ui.value.userId
                    val roleId = authViewModel.ui.value.roleId
                    if (userId != null) {
                        // Asegurar que no queden restos de un resumen de compra anterior
                        cartViewModel?.clearPurchaseSummary()
                        // Inicializar carrito para el usuario actual
                        cartViewModel?.loadCartForUser(userId)
                    }
                    when (roleId) {
                        2 -> navController.navigate("home_admin") {
                            popUpTo("login") { inclusive = true }
                        }
                        else -> navController.navigate("home_user") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            SignupRoute(
                viewModel = authViewModel,
                onSignupSuccess = { roleId ->
                    when (roleId) {
                        2 -> navController.navigate("home_admin") {
                            popUpTo(0) { inclusive = true }
                        }
                        else -> navController.navigate("home_user") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // Home del usuario: catálogo de servicios
        composable("home_user") {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            ServicesRoute(
                viewModel = null,
                cartViewModel = cartViewModel,
                onAgregarCarrito = { servicio ->
                    // La lógica del carrito ya está implementada en ServicesRoute
                },
                onLogout = {
                    // Ejecutar logout de forma segura: borrar session DataStore primero, luego limpiar CartViewModel y navegar
                    scope.launch {
                        try {
                            // Llamar al repo/logout del AuthViewModel (limpia internamente DataStore)
                            authViewModel.logout()
                        } catch (_: Exception) {}

                        // Asegurar limpieza síncrona del DataStore como respaldo
                        try {
                            SessionDebugHelper.clearSessionSync(context)
                        } catch (_: Exception) {}

                        // Limpiar resumen de compra local
                        cartViewModel?.clearPurchaseSummary()

                        // Navegar a login limpiando backstack
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onOpenCart = { navController.navigate("cart") },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        // Home del administrador
        composable("home_admin") {
            AdminHomeScreen(
                navController = navController,
                authViewModel = authViewModel,
                cartViewModel = cartViewModel
            )
        }

        composable("cart") {
            CartRoute(navController = navController, viewModel = cartViewModel)
        }
        composable("purchase_summary") {
            // Mostrar resumen usando la instancia compartida del CartViewModel
            com.antaedo_karfuentealba.eva3_ambientefest.ui.cart.PurchaseSummaryScreen(
                cartViewModel = cartViewModel,
                onBack = { navController.navigate("home_user") { popUpTo("home_user") { inclusive = true } } }
            )
        }
    }
}