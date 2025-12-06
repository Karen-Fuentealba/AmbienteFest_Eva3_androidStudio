package com.antaedo_karfuentealba.eva3_ambientefest.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.UserResponse
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorContenido
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.UserManagementUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserDialog(
    uiState: UserManagementUiState,
    onDismiss: () -> Unit,
    onFieldChanged: (String?, String?, String?, String?, Int?) -> Unit,
    onCreate: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Crear Nuevo Administrador",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Nombre
                OutlinedTextField(
                    value = uiState.createUserName,
                    onValueChange = { onFieldChanged(it, null, null, null, null) },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Apellido
                OutlinedTextField(
                    value = uiState.createUserLastName,
                    onValueChange = { onFieldChanged(null, it, null, null, null) },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Email
                OutlinedTextField(
                    value = uiState.createUserEmail,
                    onValueChange = { onFieldChanged(null, null, it, null, null) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Contraseña
                OutlinedTextField(
                    value = uiState.createUserPassword,
                    onValueChange = { onFieldChanged(null, null, null, it, null) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Texto explicativo sobre la creación de usuarios
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ColorPrincipal.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = " Solo puedes crear usuarios administradores .",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorContenido.copy(alpha = 0.8f)
                    )
                }


                Spacer(Modifier.height(16.dp))

                // Mensaje de error
                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = ColorContenido)
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = onCreate,
                        enabled = !uiState.loading,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrincipal)
                    ) {
                        if (uiState.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Crear", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDialog(
    uiState: UserManagementUiState,
    onDismiss: () -> Unit,
    onFieldChanged: (String?, String?, String?, Int?) -> Unit,
    onUpdate: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Editar Usuario",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Nombre
                OutlinedTextField(
                    value = uiState.editUserName,
                    onValueChange = { onFieldChanged(it, null, null, null) },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Apellido
                OutlinedTextField(
                    value = uiState.editUserLastName,
                    onValueChange = { onFieldChanged(null, it, null, null) },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Email
                OutlinedTextField(
                    value = uiState.editUserEmail,
                    onValueChange = { onFieldChanged(null, null, it, null) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Selector de rol
                var expanded by remember { mutableStateOf(false) }
                val selectedRole = uiState.roles.find { it.id == uiState.editUserRoleId }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedRole?.name ?: "Seleccionar rol",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Rol") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorPrincipal,
                            focusedLabelColor = ColorPrincipal
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        uiState.roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.name) },
                                onClick = {
                                    onFieldChanged(null, null, null, role.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Mensaje de error
                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = ColorContenido)
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = onUpdate,
                        enabled = !uiState.loading,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrincipal)
                    ) {
                        if (uiState.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Actualizar", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteUserDialog(
    user: UserResponse?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    user?.let {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Confirmar eliminación",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ColorPrincipal
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "¿Estás seguro de que deseas eliminar al usuario ${user.name ?: ""} ${user.lastName ?: ""} (${user.email})?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorContenido
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar", color = ColorContenido)
                        }

                        Spacer(Modifier.width(8.dp))

                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Eliminar", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
