package com.antaedo_karfuentealba.eva3_ambientefest.ui.auth

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.AuthViewModel

@Composable
fun SignupRoute(
    viewModel: AuthViewModel,
    onSignupSuccess: (roleId: Int?) -> Unit,
    onBackToLogin: () -> Unit
) {
    val ui by viewModel.ui.collectAsState()
    val context = LocalContext.current
    var clearFieldsTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(ui.registrationSuccess) {
        if (ui.registrationSuccess) {
            Toast.makeText(context, ui.infoMessage ?: "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
            clearFieldsTrigger = true
            // Después del registro, normalmente vamos al login
            onBackToLogin()
            viewModel.clearRegistrationFlag()
        }
    }

    LaunchedEffect(ui.errorMessage) {
        ui.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    SignupScreen(
        onRegister = { nombre, apellido, email, password ->
            viewModel.onNameChanged(nombre)
            viewModel.onLastNameChanged(apellido)
            viewModel.onEmailChanged(email)
            viewModel.onPasswordChanged(password)
            viewModel.signup()
        },
        onNavigateToLogin = onBackToLogin,
        registerResultMessage = when {
            ui.loading -> "Registrando..."
            ui.registrationSuccess -> ui.infoMessage
            ui.errorMessage != null -> ui.errorMessage
            else -> null
        },
        clearFieldsTrigger = clearFieldsTrigger,
        onFieldsCleared = { clearFieldsTrigger = false }
    )
}