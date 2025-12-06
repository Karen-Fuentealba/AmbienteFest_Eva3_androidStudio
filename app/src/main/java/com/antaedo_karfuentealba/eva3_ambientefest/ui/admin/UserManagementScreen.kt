package com.antaedo_karfuentealba.eva3_ambientefest.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.UserResponse
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorContenido
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.UserManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    navController: NavController,
    viewModel: UserManagementViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            kotlinx.coroutines.delay(5000)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestión de Usuarios",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FontFamily.Serif,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorPrincipal
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateUserDialog() },
                containerColor = ColorPrincipal
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar Usuario",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Título de bienvenida
            Text(
                "AmbienteFest",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Cursive,
                    color = ColorPrincipal
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Administrar usuarios del sistema",
                style = MaterialTheme.typography.bodyLarge,
                color = ColorContenido.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            // Mensajes de estado
            uiState.successMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Green.copy(alpha = 0.8f)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            uiState.errorMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Red.copy(alpha = 0.8f)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // Contenido principal
            if (uiState.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorPrincipal)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.users,
                        key = { user -> "${user.id}_${user.roleId}_${user.name}_${user.email}" }
                    ) { user ->
                        UserCard(
                            user = user,
                            roles = uiState.roles,
                            onEdit = { viewModel.showEditUserDialog(user) },
                            onDelete = { viewModel.showDeleteUserDialog(user) }
                        )
                    }
                }
            }
        }
    }

    // Diálogos
    if (uiState.showCreateDialog) {
        CreateUserDialog(
            uiState = uiState,
            onDismiss = { viewModel.hideCreateUserDialog() },
            onFieldChanged = viewModel::onCreateUserFieldChanged,
            onCreate = { viewModel.createUser() }
        )
    }

    if (uiState.showEditDialog) {
        EditUserDialog(
            uiState = uiState,
            onDismiss = { viewModel.hideEditUserDialog() },
            onFieldChanged = viewModel::onEditUserFieldChanged,
            onUpdate = { viewModel.updateUser() }
        )
    }

    if (uiState.showDeleteDialog) {
        DeleteUserDialog(
            user = uiState.selectedUser,
            onDismiss = { viewModel.hideDeleteUserDialog() },
            onConfirm = { viewModel.deleteUser() }
        )
    }
}

@Composable
private fun UserCard(
    user: UserResponse,
    roles: List<com.antaedo_karfuentealba.eva3_ambientefest.data.model.Role>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Forzar recomposición cuando cambie el rol del usuario
    val roleName by remember(user.roleId, roles) {
        derivedStateOf {
            roles.find { it.id == user.roleId }?.name ?: "Sin rol"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${user.name ?: ""} ${user.lastName ?: ""}".trim(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ColorPrincipal
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorContenido.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Rol: $roleName",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorContenido.copy(alpha = 0.6f)
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = ColorPrincipal
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}
