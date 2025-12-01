package com.antaedo_karfuentealba.eva3_ambientefest.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorContenido
import com.antaedo_karfuentealba.eva3_ambientefest.ui.components.LoadingIndicator

@Composable
fun SignupScreen(
    onRegister: (nombre: String, apellido: String, email: String, password: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    registerResultMessage: String?,
    clearFieldsTrigger: Boolean,
    onFieldsCleared: () -> Unit,
    isLoading: Boolean = false
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(clearFieldsTrigger) {
        if (clearFieldsTrigger) {
            nombre = ""
            apellido = ""
            email = ""
            password = ""
            confirm = ""
            onFieldsCleared()
            localError = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

            // Campos de registro
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (localError != null) localError = null
                },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = confirm,
                onValueChange = {
                    confirm = it
                    if (localError != null) localError = null
                },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(16.dp))

            // Botón Registrar con ColorPrincipal
            Button(
                onClick = {
                    if (password != confirm) {
                        localError = "Las contraseñas no coinciden"
                    } else if (email.isBlank() || password.isBlank() || apellido.isBlank() || nombre.isBlank()) {
                        localError = "Todos los campos son obligatorios"
                    } else {
                        onRegister(nombre, apellido, email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrincipal,
                    contentColor = Color.White
                )
            ) {
                Text(if (isLoading) "Registrando..." else "Registrar")
            }

            Spacer(Modifier.height(8.dp))

            // TextButton "Inicia sesión" con ColorContenido
            TextButton(
                onClick = onNavigateToLogin,
                enabled = !isLoading
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = ColorContenido)
            }

            // Mensajes de error y resultado
            registerResultMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    color = if (it.contains("exitoso")) Color.Green else Color.Red
                )
            }

            localError?.let {
                Spacer(Modifier.height(8.dp))
                Text(text = it, color = Color.Red)
            }
        }
        
        // Indicador de carga superpuesto
        if (isLoading) {
            LoadingIndicator(
                message = "Creando cuenta...",
                isVisible = true
            )
        }
    }
}
