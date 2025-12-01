package com.antaedo_karfuentealba.eva3_ambientefest.ui.auth

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.AuthViewModel

@Composable
fun LoginRoute(
    viewModel: AuthViewModel,
    onLoginSuccess: (roleId: Int?) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val ui by viewModel.ui.collectAsState()
    val context = LocalContext.current

    // Toasts para login result
    var handledLogin by remember { mutableStateOf(false) }
    LaunchedEffect(ui.loggedIn) {
        if (ui.loggedIn && !handledLogin) {
            Toast.makeText(context, ui.infoMessage ?: "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
            handledLogin = true
            onLoginSuccess(ui.roleId)
        }
    }

    LaunchedEffect(ui.errorMessage) {
        ui.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LoginScreen(
        onLogin = { email, password ->
            viewModel.onEmailChanged(email)
            viewModel.onPasswordChanged(password)
            viewModel.login()
        },
        onNavigateToRegister = onNavigateToRegister,
        loginResultMessage = when {
            ui.loading && !ui.loggedIn -> "Validando..."
            ui.loggedIn -> null
            ui.errorMessage != null -> ui.errorMessage
            else -> null
        }
    )
}