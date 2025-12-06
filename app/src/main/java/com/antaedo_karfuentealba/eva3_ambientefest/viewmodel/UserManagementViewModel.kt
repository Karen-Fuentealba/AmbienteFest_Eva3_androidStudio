package com.antaedo_karfuentealba.eva3_ambientefest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Role
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.UserResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserManagementUiState(
    val users: List<UserResponse> = emptyList(),
    val roles: List<Role> = emptyList(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showCreateDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val selectedUser: UserResponse? = null,
    val createUserName: String = "",
    val createUserLastName: String = "",
    val createUserEmail: String = "",
    val createUserPassword: String = "",
    val createUserRoleId: Int? = null,
    val editUserName: String = "",
    val editUserLastName: String = "",
    val editUserEmail: String = "",
    val editUserRoleId: Int? = null
)

class UserManagementViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState

    init {
        loadUsersAndRoles()
    }

    private fun loadUsersAndRoles() {
        _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val usersResult = userRepository.getAllUsers()
                val rolesResult = userRepository.getAllRoles()

                if (usersResult.isSuccess && rolesResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        users = usersResult.getOrNull() ?: emptyList(),
                        roles = rolesResult.getOrNull() ?: emptyList(),
                        loading = false
                    )
                } else {
                    val error = usersResult.exceptionOrNull()?.message
                        ?: rolesResult.exceptionOrNull()?.message
                        ?: "Error al cargar datos"
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        errorMessage = error
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun refreshData() {
        loadUsersAndRoles()
    }

    // Crear usuario
    fun showCreateUserDialog() {
        // Auto-seleccionar rol de administrador (roleId = 2) por defecto
        val adminRoleId = _uiState.value.roles.find { it.id == 2 }?.id ?: 2

        _uiState.value = _uiState.value.copy(
            showCreateDialog = true,
            createUserName = "",
            createUserLastName = "",
            createUserEmail = "",
            createUserPassword = "",
            createUserRoleId = adminRoleId, // Pre-seleccionar administrador
            errorMessage = null,
            successMessage = null
        )
    }

    fun hideCreateUserDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun onCreateUserFieldChanged(
        name: String? = null,
        lastName: String? = null,
        email: String? = null,
        password: String? = null,
        roleId: Int? = null
    ) {
        _uiState.value = _uiState.value.copy(
            createUserName = name ?: _uiState.value.createUserName,
            createUserLastName = lastName ?: _uiState.value.createUserLastName,
            createUserEmail = email ?: _uiState.value.createUserEmail,
            createUserPassword = password ?: _uiState.value.createUserPassword,
            createUserRoleId = roleId ?: _uiState.value.createUserRoleId,
            errorMessage = null
        )
    }

    fun createUser() {
        val state = _uiState.value

        if (state.createUserName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El nombre es requerido")
            return
        }
        if (state.createUserEmail.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El email es requerido")
            return
        }
        if (state.createUserPassword.isBlank()) {
            _uiState.value = state.copy(errorMessage = "La contraseña es requerida")
            return
        }
        if (state.createUserRoleId == null) {
            _uiState.value = state.copy(errorMessage = "Debe seleccionar un rol")
            return
        }

        // Validar que solo se puedan crear usuarios administradores
        if (state.createUserRoleId != 2) {
            _uiState.value = state.copy(errorMessage = "Solo se pueden crear usuarios administradores desde este panel")
            return
        }

        _uiState.value = state.copy(loading = true, errorMessage = null)

        viewModelScope.launch {
            val result = userRepository.createUser(
                name = state.createUserName,
                lastName = state.createUserLastName.takeIf { it.isNotBlank() },
                email = state.createUserEmail,
                password = state.createUserPassword,
                roleId = state.createUserRoleId
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    showCreateDialog = false,
                    successMessage = "Usuario creado exitosamente"
                )
                loadUsersAndRoles()
            } else {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al crear usuario"
                )
            }
        }
    }

    // Editar usuario
    fun showEditUserDialog(user: UserResponse) {
        _uiState.value = _uiState.value.copy(
            showEditDialog = true,
            selectedUser = user,
            editUserName = user.name ?: "",
            editUserLastName = user.lastName ?: "",
            editUserEmail = user.email,
            editUserRoleId = user.roleId,
            errorMessage = null,
            successMessage = null
        )
    }

    fun hideEditUserDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = false, selectedUser = null)
    }

    fun onEditUserFieldChanged(
        name: String? = null,
        lastName: String? = null,
        email: String? = null,
        roleId: Int? = null
    ) {
        _uiState.value = _uiState.value.copy(
            editUserName = name ?: _uiState.value.editUserName,
            editUserLastName = lastName ?: _uiState.value.editUserLastName,
            editUserEmail = email ?: _uiState.value.editUserEmail,
            editUserRoleId = roleId ?: _uiState.value.editUserRoleId,
            errorMessage = null
        )
    }

    fun updateUser() {
        val state = _uiState.value
        val user = state.selectedUser ?: return

        if (state.editUserName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El nombre es requerido")
            return
        }
        if (state.editUserEmail.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El email es requerido")
            return
        }

        _uiState.value = state.copy(loading = true, errorMessage = null)

        viewModelScope.launch {
            val result = userRepository.updateUser(
                userId = user.id,
                name = state.editUserName,
                lastName = state.editUserLastName.takeIf { it.isNotBlank() },
                email = state.editUserEmail,
                roleId = state.editUserRoleId
            )

            if (result.isSuccess) {
                val updatedUser = result.getOrNull()

                // Actualizar inmediatamente la lista local con los datos actualizados
                if (updatedUser != null) {
                    val updatedUsers = _uiState.value.users.map { existingUser ->
                        if (existingUser.id == updatedUser.id) updatedUser else existingUser
                    }
                    _uiState.value = _uiState.value.copy(
                        users = updatedUsers,
                        loading = false,
                        showEditDialog = false,
                        successMessage = "Usuario actualizado exitosamente"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        showEditDialog = false,
                        successMessage = "Usuario actualizado exitosamente"
                    )
                    // Si no tenemos el usuario actualizado, refrescar desde el servidor
                    loadUsersAndRoles()
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al actualizar usuario"
                )
            }
        }
    }

    // Eliminar usuario
    fun showDeleteUserDialog(user: UserResponse) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            selectedUser = user,
            errorMessage = null
        )
    }

    fun hideDeleteUserDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false, selectedUser = null)
    }

    fun deleteUser() {
        val user = _uiState.value.selectedUser ?: return

        _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)

        viewModelScope.launch {
            val result = userRepository.deleteUser(user.id)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    showDeleteDialog = false,
                    successMessage = "Usuario eliminado exitosamente"
                )
                loadUsersAndRoles()
            } else {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al eliminar usuario"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    companion object {
        fun Factory(userRepository: UserRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserManagementViewModel(userRepository) as T
            }
        }
    }
}
