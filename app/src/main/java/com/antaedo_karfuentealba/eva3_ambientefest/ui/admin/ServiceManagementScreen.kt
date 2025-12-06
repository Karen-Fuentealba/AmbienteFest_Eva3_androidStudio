package com.antaedo_karfuentealba.eva3_ambientefest.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.XanoImage
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.Graph
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorContenido
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.ServiceManagementViewModel
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.ServiceTimeSlotViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceManagementScreen(
    navController: NavController,
    viewModel: ServiceManagementViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Agregar TimeSlot ViewModel
    val timeSlotViewModel: ServiceTimeSlotViewModel = viewModel(
        factory = ServiceTimeSlotViewModel.Factory(Graph.serviceRepository)
    )
    val timeSlotUiState by timeSlotViewModel.uiState.collectAsState()

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

    // Mensajes de éxito para time slots
    timeSlotUiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(3000)
            timeSlotViewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestión de Servicios",
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
                onClick = { viewModel.showCreateServiceDialog() },
                containerColor = ColorPrincipal
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar Servicio",
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
                text = "Administrar servicios del sistema",
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

            // Mensajes de éxito para time slots
            timeSlotUiState.successMessage?.let { message ->
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.services,
                        key = { service -> "${service.id}_${service.name}_${service.price}_${service.imagen.size}" }
                    ) { service ->
                        ServiceCard(
                            service = service,
                            onEdit = { viewModel.showEditServiceDialog(service) },
                            onDelete = { viewModel.showDeleteServiceDialog(service) },
                            onListTimeSlots = { id, name -> timeSlotViewModel.showTimeSlotsDialog(id) },
                            onCreateTimeSlot = { id -> timeSlotViewModel.showCreateTimeSlotDialog(id) }
                        )
                    }
                }
            }
        }
    }

    // Diálogos
    if (uiState.showCreateDialog) {
        CreateServiceDialog(
            uiState = uiState,
            onDismiss = { viewModel.hideCreateServiceDialog() },
            onFieldChanged = viewModel::onCreateServiceFieldChanged,
            onCreate = { viewModel.createService() },
            onUploadImages = { parts -> viewModel.uploadImages(parts, isForEdit = false) },
            onRemoveImage = { image -> viewModel.removeImage(image, isForEdit = false) }
        )
    }

    if (uiState.showEditDialog) {
        EditServiceDialog(
            uiState = uiState,
            onDismiss = { viewModel.hideEditServiceDialog() },
            onFieldChanged = viewModel::onEditServiceFieldChanged,
            onUpdate = { viewModel.updateService() },
            onUploadImages = { parts -> viewModel.uploadImages(parts, isForEdit = true) },
            onRemoveImage = { image -> viewModel.removeImage(image, isForEdit = true) }
        )
    }

    if (uiState.showDeleteDialog) {
        DeleteServiceDialog(
            service = uiState.selectedService,
            onDismiss = { viewModel.hideDeleteServiceDialog() },
            onConfirm = { viewModel.deleteService() }
        )
    }

    // Diálogos de time slots
    if (timeSlotUiState.showTimeSlotsDialog && timeSlotUiState.selectedServiceId != null) {
        val service = uiState.services.find { it.id == timeSlotUiState.selectedServiceId }
        if (service != null) {
            TimeSlotListDialog(
                serviceId = service.id,
                serviceName = service.name,
                uiState = timeSlotUiState,
                onDismiss = { timeSlotViewModel.hideTimeSlotsDialog() },
                onEditTimeSlot = { timeSlot -> timeSlotViewModel.showEditTimeSlotDialog(timeSlot) },
                onDeleteTimeSlot = { timeSlot -> timeSlotViewModel.showDeleteTimeSlotDialog(timeSlot) },
                onCreateTimeSlot = { timeSlotViewModel.showCreateTimeSlotDialog(service.id) }
            )
        }
    }

    if (timeSlotUiState.showCreateTimeSlotDialog) {
        CreateTimeSlotDialog(
            uiState = timeSlotUiState,
            onDismiss = { timeSlotViewModel.hideCreateTimeSlotDialog() },
            onFieldChanged = timeSlotViewModel::onCreateTimeSlotFieldChanged,
            onCreate = { timeSlotViewModel.createTimeSlot() }
        )
    }

    if (timeSlotUiState.showEditTimeSlotDialog) {
        EditTimeSlotDialog(
            uiState = timeSlotUiState,
            onDismiss = { timeSlotViewModel.hideEditTimeSlotDialog() },
            onFieldChanged = timeSlotViewModel::onEditTimeSlotFieldChanged,
            onUpdate = { timeSlotViewModel.updateTimeSlot() }
        )
    }

    if (timeSlotUiState.showDeleteTimeSlotDialog) {
        DeleteTimeSlotDialog(
            timeSlot = timeSlotUiState.selectedTimeSlot,
            onDismiss = { timeSlotViewModel.hideDeleteTimeSlotDialog() },
            onConfirm = { timeSlotViewModel.deleteTimeSlot() }
        )
    }
}

@Composable
private fun ServiceCard(
    service: Servicio,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onListTimeSlots: (Int, String) -> Unit,
    onCreateTimeSlot: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        // Contenedor principal con contenido y columna de íconos al extremo derecho
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Contenido del servicio (izquierda)
            Column(modifier = Modifier.weight(1f)) {
                // Nombre (título)
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Descripción justo después del título
                Spacer(Modifier.height(8.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorContenido.copy(alpha = 0.8f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                // Precio (vertical item)
                val intPrice = (service.price ?: 0.0).toInt()
                val formattedPrice = intPrice.toString().reversed().chunked(3).joinToString(".").reversed()
                Text(
                    text = "Precio: $formattedPrice",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ColorPrincipal
                    )
                )

                Spacer(Modifier.height(6.dp))

                // Disponible (vertical item, solo si hay dato)
                val availabilityText = service.availability?.takeIf { it.isNotBlank() }
                    ?: service.status?.takeIf { it.isNotBlank() }
                if (availabilityText != null) {
                    Text(
                        text = "Disponible:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ColorContenido
                    )
                    Text(
                        text = availabilityText,
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorContenido.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(6.dp))
                }

                // Proveedor (vertical item)
                Text(
                    text = "Proveedor:",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = ColorContenido
                )
                Text(
                    text = service.provider,
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorContenido.copy(alpha = 0.8f)
                )

                Spacer(Modifier.height(10.dp))

                // Fila única: Imágenes + Categoría + Valoración
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Imágenes (izquierda)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Imágenes (${service.imagen.size})",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorContenido.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(service.imagen) { image ->
                                ServiceImageThumbnail(image = image)
                            }
                        }
                    }

                    // Categoría (centro)
                    val categoryText = service.service_category_id?.let { "Categoría: $it" } ?: "Categoría: -"
                    Text(
                        text = categoryText,
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorContenido.copy(alpha = 0.8f),
                        modifier = Modifier.weight(0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Valoración (derecha)
                    Row(
                        modifier = Modifier.weight(0.6f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        service.rating?.let { rating ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", rating),
                                style = MaterialTheme.typography.bodySmall,
                                color = ColorContenido.copy(alpha = 0.8f)
                            )
                            service.num_ratings?.let { count ->
                                Text(
                                    text = " ($count)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorContenido.copy(alpha = 0.6f)
                                )
                            }
                        } ?: run {
                            Text(
                                text = "Sin valoración",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColorContenido.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            // Columna vertical de íconos (derecha)
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Editar
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar servicio",
                        tint = ColorPrincipal
                    )
                }
                // Eliminar
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar servicio",
                        tint = Color.Red
                    )
                }
                // Listar horarios
                IconButton(
                    onClick = { onListTimeSlots(service.id, service.name) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "Listar horarios",
                        tint = ColorContenido
                    )
                }
                // Crear horario
                IconButton(
                    onClick = { onCreateTimeSlot(service.id) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Crear horario",
                        tint = ColorPrincipal
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceImageThumbnail(
    image: XanoImage,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = image.path ?: "",
        contentDescription = "Imagen del servicio",
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}
