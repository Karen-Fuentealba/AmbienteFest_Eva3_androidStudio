package com.antaedo_karfuentealba.eva3_ambientefest.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.TimeSlot
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorContenido
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.ServiceTimeSlotUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotListDialog(
    serviceId: Int,
    serviceName: String,
    uiState: ServiceTimeSlotUiState,
    onDismiss: () -> Unit,
    onEditTimeSlot: (TimeSlot) -> Unit,
    onDeleteTimeSlot: (TimeSlot) -> Unit,
    onCreateTimeSlot: () -> Unit
) {
    val timeSlots = uiState.timeSlotsByService[serviceId] ?: emptyList()
    val isLoading = uiState.loadingByService[serviceId] ?: false
    val errorMessage = uiState.errorByService[serviceId]

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Horarios de $serviceName",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ColorPrincipal
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = ColorContenido
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Botón crear horario
                Button(
                    onClick = onCreateTimeSlot,
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrincipal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear Nuevo Horario", color = Color.White)
                }

                Spacer(Modifier.height(16.dp))

                // Contenido
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ColorPrincipal)
                        }
                    }

                    errorMessage != null -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    timeSlots.isEmpty() -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ColorPrincipal.copy(alpha = 0.1f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Este servicio no tiene horarios disponibles",
                                style = MaterialTheme.typography.bodyLarge,
                                color = ColorContenido,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(timeSlots) { timeSlot ->
                                TimeSlotCard(
                                    timeSlot = timeSlot,
                                    onEdit = { onEditTimeSlot(timeSlot) },
                                    onDelete = { onDeleteTimeSlot(timeSlot) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSlotCard(
    timeSlot: TimeSlot,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = timeSlot.date,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${timeSlot.start_time} → ${timeSlot.end_time}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorContenido
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar horario",
                        tint = ColorPrincipal
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar horario",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTimeSlotDialog(
    uiState: ServiceTimeSlotUiState,
    onDismiss: () -> Unit,
    onFieldChanged: (String?, String?, String?) -> Unit,
    onCreate: () -> Unit
) {
    val serviceId = uiState.selectedServiceId ?: return

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
                    text = "Crear Nuevo Horario",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Fecha
                OutlinedTextField(
                    value = uiState.createDate,
                    onValueChange = { onFieldChanged(it, null, null) },
                    label = { Text("Fecha (YYYY-MM-DD)") },
                    placeholder = { Text("2024-12-25") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Hora inicio
                OutlinedTextField(
                    value = uiState.createStartTime,
                    onValueChange = { onFieldChanged(null, it, null) },
                    label = { Text("Hora de inicio (HH:MM)") },
                    placeholder = { Text("10:00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Hora fin
                OutlinedTextField(
                    value = uiState.createEndTime,
                    onValueChange = { onFieldChanged(null, null, it) },
                    label = { Text("Hora de fin (HH:MM)") },
                    placeholder = { Text("11:00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Mensaje de error
                uiState.errorByService[serviceId]?.let { error ->
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
                        enabled = !uiState.globalLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrincipal)
                    ) {
                        if (uiState.globalLoading) {
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
fun EditTimeSlotDialog(
    uiState: ServiceTimeSlotUiState,
    onDismiss: () -> Unit,
    onFieldChanged: (String?, String?, String?) -> Unit,
    onUpdate: () -> Unit
) {
    val timeSlot = uiState.selectedTimeSlot ?: return
    val serviceId = uiState.selectedServiceId ?: return

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
                    text = "Editar Horario",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Fecha
                OutlinedTextField(
                    value = uiState.editDate,
                    onValueChange = { onFieldChanged(it, null, null) },
                    label = { Text("Fecha (YYYY-MM-DD)") },
                    placeholder = { Text("2024-12-25") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Hora inicio
                OutlinedTextField(
                    value = uiState.editStartTime,
                    onValueChange = { onFieldChanged(null, it, null) },
                    label = { Text("Hora de inicio (HH:MM)") },
                    placeholder = { Text("10:00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Hora fin
                OutlinedTextField(
                    value = uiState.editEndTime,
                    onValueChange = { onFieldChanged(null, null, it) },
                    label = { Text("Hora de fin (HH:MM)") },
                    placeholder = { Text("11:00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Mensaje de error
                uiState.errorByService[serviceId]?.let { error ->
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
                        enabled = !uiState.globalLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrincipal)
                    ) {
                        if (uiState.globalLoading) {
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
fun DeleteTimeSlotDialog(
    timeSlot: TimeSlot?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    timeSlot?.let {
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
                        text = "¿Estás seguro de que deseas eliminar el horario del ${timeSlot.date} de ${timeSlot.start_time} a ${timeSlot.end_time}?",
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
