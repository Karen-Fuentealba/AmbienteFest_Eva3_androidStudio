package com.antaedo_karfuentealba.eva3_ambientefest.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.XanoImage
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorContenido
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.ServiceManagementUiState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceDialog(
    uiState: ServiceManagementUiState,
    onDismiss: () -> Unit,
    onFieldChanged: (String?, String?, String?, String?, String?, String?, List<XanoImage>?) -> Unit,
    onCreate: () -> Unit,
    onUploadImages: (List<MultipartBody.Part>) -> Unit,
    onRemoveImage: (XanoImage) -> Unit
) {
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val parts = uris.mapNotNull { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val tempFile = File.createTempFile("image", ".jpg", context.cacheDir)
                    val outputStream = FileOutputStream(tempFile)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()

                    val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
                } catch (e: Exception) {
                    null
                }
            }
            onUploadImages(parts)
        }
    }

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
                    text = "Crear Nuevo Servicio",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.createServiceName,
                    onValueChange = { onFieldChanged(it, null, null, null, null, null, null) },
                    label = { Text("Nombre del servicio") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.createServiceDescription,
                    onValueChange = { onFieldChanged(null, it, null, null, null, null, null) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.createServicePrice,
                    onValueChange = { onFieldChanged(null, null, it, null, null, null, null) },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.createServiceProvider,
                    onValueChange = { onFieldChanged(null, null, null, it, null, null, null) },
                    label = { Text("Proveedor") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                var expandedAvailability by remember { mutableStateOf(false) }
                val availabilityOptions = listOf("Disponible", "No disponible", "En mantenimiento")

                ExposedDropdownMenuBox(
                    expanded = expandedAvailability,
                    onExpandedChange = { expandedAvailability = !expandedAvailability }
                ) {
                    OutlinedTextField(
                        value = uiState.createServiceAvailability,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Disponibilidad") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorPrincipal,
                            focusedLabelColor = ColorPrincipal
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAvailability) }
                    )

                    ExposedDropdownMenu(
                        expanded = expandedAvailability,
                        onDismissRequest = { expandedAvailability = false }
                    ) {
                        availabilityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onFieldChanged(null, null, null, null, option, null, null)
                                    expandedAvailability = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Imágenes del servicio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    enabled = !uiState.uploadingImages,
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrincipal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.uploadingImages) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Seleccionar imágenes")
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (uiState.createServiceImages.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.createServiceImages) { image ->
                            ImageThumbnailWithRemove(
                                image = image,
                                onRemove = { onRemoveImage(image) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                }

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
                        enabled = !uiState.loading && !uiState.uploadingImages,
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
fun EditServiceDialog(
    uiState: ServiceManagementUiState,
    onDismiss: () -> Unit,
    onFieldChanged: (String?, String?, String?, String?, String?, String?, List<XanoImage>?) -> Unit,
    onUpdate: () -> Unit,
    onUploadImages: (List<MultipartBody.Part>) -> Unit,
    onRemoveImage: (XanoImage) -> Unit
) {
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val parts = uris.mapNotNull { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val tempFile = File.createTempFile("image", ".jpg", context.cacheDir)
                    val outputStream = FileOutputStream(tempFile)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()

                    val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
                } catch (e: Exception) {
                    null
                }
            }
            onUploadImages(parts)
        }
    }

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
                    text = "Editar Servicio",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.editServiceName,
                    onValueChange = { onFieldChanged(it, null, null, null, null, null, null) },
                    label = { Text("Nombre del servicio") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.editServiceDescription,
                    onValueChange = { onFieldChanged(null, it, null, null, null, null, null) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.editServicePrice,
                    onValueChange = { onFieldChanged(null, null, it, null, null, null, null) },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.editServiceProvider,
                    onValueChange = { onFieldChanged(null, null, null, it, null, null, null) },
                    label = { Text("Proveedor") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorPrincipal,
                        focusedLabelColor = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                var expandedAvailability by remember { mutableStateOf(false) }
                val availabilityOptions = listOf("Disponible", "No disponible", "En mantenimiento")

                ExposedDropdownMenuBox(
                    expanded = expandedAvailability,
                    onExpandedChange = { expandedAvailability = !expandedAvailability }
                ) {
                    OutlinedTextField(
                        value = uiState.editServiceAvailability,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Disponibilidad") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorPrincipal,
                            focusedLabelColor = ColorPrincipal
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAvailability) }
                    )

                    ExposedDropdownMenu(
                        expanded = expandedAvailability,
                        onDismissRequest = { expandedAvailability = false }
                    ) {
                        availabilityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onFieldChanged(null, null, null, null, option, null, null)
                                    expandedAvailability = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Imágenes del servicio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    enabled = !uiState.uploadingImages,
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrincipal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.uploadingImages) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Agregar más imágenes")
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (uiState.editServiceImages.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.editServiceImages) { image ->
                            ImageThumbnailWithRemove(
                                image = image,
                                onRemove = { onRemoveImage(image) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                }

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
                        enabled = !uiState.loading && !uiState.uploadingImages,
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
fun DeleteServiceDialog(
    service: Servicio?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    service?.let {
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
                        text = "¿Estás seguro de que deseas eliminar el servicio \"${service.name}\"? Esta acción no se puede deshacer.",
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

@Composable
private fun ImageThumbnailWithRemove(
    image: XanoImage,
    onRemove: () -> Unit
) {
    Box {
        AsyncImage(
            model = image.path ?: "",
            contentDescription = "Imagen del servicio",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Eliminar imagen",
                tint = Color.Red,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
