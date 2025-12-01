// viewmodel/AuthViewModel.kt
package com.antaedo_karfuentealba.eva3_ambientefest.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estado de UI para autenticación. La UI debe observar este StateFlow y mostrar
 * mensajes (error/info) y estados (loading, loggedIn, registrationSuccess).
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val lastName: String = "",
    val loading: Boolean = false,
    val errorMessage: String? = null,    // Mensajes de error para mostrar bajo los campos
    val infoMessage: String? = null,     // Mensajes de información / éxito
    val loggedIn: Boolean = false,
    val welcomeMessage: String? = null,  // "Bienvenido, ... 🔐"
    val userId: Int? = null,
    val roleId: Int? = null,
    val registrationSuccess: Boolean = false // bandera para redirigir a Login
)

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui

    // --- UI field changes (UI components llaman a estas funciones) ---
    fun onEmailChanged(value: String) {
        _ui.value = _ui.value.copy(email = value, errorMessage = null, infoMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _ui.value = _ui.value.copy(password = value, errorMessage = null, infoMessage = null)
    }

    fun onNameChanged(value: String) {
        _ui.value = _ui.value.copy(name = value, errorMessage = null, infoMessage = null)
    }

    fun onLastNameChanged(value: String) {
        _ui.value = _ui.value.copy(lastName = value, errorMessage = null, infoMessage = null)
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // --- Login ---
    fun login() {
        val state = _ui.value

        // Validaciones locales antes de llamar al repo
        if (state.email.isBlank()) {
            _ui.value = state.copy(errorMessage = "Ingresa un correo.")
            return
        }

        if (!isEmailValid(state.email)) {
            _ui.value = state.copy(errorMessage = "Formato de correo inválido.")
            return
        }

        if (state.password.isBlank()) {
            _ui.value = state.copy(errorMessage = "Ingresa una contraseña.")
            return
        }

        _ui.value = state.copy(loading = true, errorMessage = null, infoMessage = null)

        viewModelScope.launch {
            try {
                val result = repo.login(state.email, state.password)
                result.onSuccess { user ->
                    _ui.value = _ui.value.copy(
                        loading = false,
                        loggedIn = true,
                        welcomeMessage = "Bienvenido, ${user.name ?: user.email} 🔐",
                        userId = user.id,
                        roleId = user.roleId,
                        infoMessage = "Bienvenido, ${user.name ?: user.email} 🔐",
                        errorMessage = null
                    )
                }.onFailure {
                    _ui.value = _ui.value.copy(
                        loading = false,
                        errorMessage = "Usuario o contraseña incorrectos.",
                        infoMessage = null
                    )
                }
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    loading = false,
                    errorMessage = e.message ?: "Error al iniciar sesión",
                    infoMessage = null
                )
            }
        }
    }

    // --- Signup / Registro ---
    fun signup() {
        val state = _ui.value

        // Validaciones locales
        if (state.name.isBlank()) {
            _ui.value = state.copy(errorMessage = "Ingresa un nombre.")
            return
        }
        if (state.lastName.isBlank()) {
            _ui.value = state.copy(errorMessage = "Ingresa un apellido.")
            return
        }
        if (state.email.isBlank()) {
            _ui.value = state.copy(errorMessage = "Ingresa un correo.")
            return
        }
        if (!isEmailValid(state.email)) {
            _ui.value = state.copy(errorMessage = "Formato de correo inválido.")
            return
        }
        if (state.password.isBlank()) {
            _ui.value = state.copy(errorMessage = "Ingresa una contraseña.")
            return
        }

        _ui.value = state.copy(loading = true, errorMessage = null, infoMessage = null)

        viewModelScope.launch {
            try {
                val result = repo.signup(state.name, state.lastName, state.email, state.password)
                result.onSuccess { user ->
                    // Registro exitoso: indicar a UI que muestre mensaje y redirija a login
                    _ui.value = _ui.value.copy(
                        loading = false,
                        registrationSuccess = true,
                        infoMessage = "Registro exitoso. Ahora puedes iniciar sesión.",
                        errorMessage = null
                    )
                }.onFailure { e ->
                    val msg = when {
                        e.message?.contains("already", ignoreCase = true) == true ||
                        e.message?.contains("exists", ignoreCase = true) == true ||
                        e.message?.contains("duplicate", ignoreCase = true) == true ||
                        e.message?.contains("409", ignoreCase = true) == true ->
                            "Este correo ya está registrado. Intenta iniciar sesión."
                        else -> e.message ?: "Error en el registro"
                    }
                    _ui.value = _ui.value.copy(
                        loading = false,
                        errorMessage = msg,
                        infoMessage = null
                    )
                }

            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    loading = false,
                    errorMessage = e.message ?: "Error en el registro",
                    infoMessage = null
                )
            }
        }
    }

    fun clearRegistrationFlag() {
        _ui.value = _ui.value.copy(registrationSuccess = false, infoMessage = null)
    }

    // --- Logout ---
    fun logout() {
        viewModelScope.launch {
            try {
                repo.logout()
            } catch (_: Exception) {
                // Ignorar errores en logout local
            }
            _ui.value = AuthUiState()
        }
    }

    // Versión suspendible para cuando se necesita esperar la limpieza antes de continuar
    suspend fun logoutSuspend() {
        try {
            repo.logout()
        } catch (_: Exception) {
            // Ignorar errores
        }
        _ui.value = AuthUiState()
    }

    companion object {
        fun Factory(repo: AuthRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repo) as T
            }
        }
    }

}