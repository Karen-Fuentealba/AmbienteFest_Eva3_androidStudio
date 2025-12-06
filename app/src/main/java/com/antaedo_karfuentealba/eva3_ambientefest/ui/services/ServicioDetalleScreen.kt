package com.antaedo_karfuentealba.eva3_ambientefest.ui.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.antaedo_karfuentealba.eva3_ambientefest.ui.components.InlineLoadingIndicator
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import com.antaedo_karfuentealba.eva3_ambientefest.ui.components.ValoracionEstrellas
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorContenido
import java.util.Locale
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.ServiceViewModel
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.TimeSlot
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.CartViewModel

@Composable
fun ServicioDetalleScreen(
    servicio: Servicio,
    onDismiss: () -> Unit,
    onAgregarCarrito: (Servicio) -> Unit,
    serviceViewModel: ServiceViewModel? = null,
    cartViewModel: CartViewModel? = null,
    userId: Int? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        val scrollState = rememberScrollState()
        var showTimeSlots by remember { mutableStateOf(false) }
        var selectedSlot by remember { mutableStateOf<TimeSlot?>(null) }
        val svcVm = serviceViewModel
        val cartVm = cartViewModel

        // Observables si se pasó ViewModel
        val timeSlots by svcVm?.timeSlots?.collectAsState() ?: remember { mutableStateOf(emptyList<TimeSlot>()) }
        val reserving by svcVm?.reserving?.collectAsState() ?: remember { mutableStateOf(false) }
        val reservedMap by svcVm?.reservedMap?.collectAsState() ?: remember { mutableStateOf(emptyMap<Int, Int>()) }
        val reservationResult by svcVm?.reservationResult?.collectAsState() ?: remember { mutableStateOf(null as? com.antaedo_karfuentealba.eva3_ambientefest.data.model.ReservationResponse?) }
        val errorState by svcVm?.error?.collectAsState() ?: remember { mutableStateOf<String?>(null) }

        val snackbarHostState = remember { SnackbarHostState() }

        // Mostrar Snackbar al ocurrir resultado o error
        LaunchedEffect(reservationResult) {
            reservationResult?.let { res ->
                snackbarHostState.showSnackbar("Horario reservado (id reserva: ${res.id}, slot: ${res.time_slot_id})")
            }
        }
        LaunchedEffect(errorState) {
            errorState?.let { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(0.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(scrollState).widthIn(max = 600.dp)) {

                // LazyRow para todas las imágenes
                if (servicio.imagen.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(servicio.imagen) { img ->
                            AsyncImage(
                                model = "https://x8ki-letl-twmt.n7.xano.io${img.path}",
                                contentDescription = servicio.name,
                                modifier = Modifier
                                    .width(240.dp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = "Sin imágenes",
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Text("Sin imágenes disponibles", color = Color.Gray)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                        .padding(24.dp)
                ) {
                    // Título del servicio
                    Text(
                        servicio.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = ColorPrincipal
                    )
                    Spacer(Modifier.height(8.dp))

                    // Proveedor
                    Text(
                        "Proveedor: ${servicio.provider}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ColorContenido
                    )
                    Spacer(Modifier.height(16.dp))

                    // Descripción
                    Text(
                        servicio.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(16.dp))

                    // Disponibilidad
                    val disponibilidad = servicio.availability ?: if (servicio.available == true) "Disponible" else "No disponible"
                    Text(
                        "Disponibilidad: $disponibilidad",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(16.dp))

                    // Rating y reseñas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ValoracionEstrellas(
                            valoracion = servicio.rating ?: 0.0
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "(${servicio.num_ratings ?: 0} reseñas)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Precio
                    val price = servicio.price.toInt()
                    val formattedPrice = "$${price.toString().reversed().chunked(3).joinToString(".").reversed()}"
                    Text(
                        text = formattedPrice,
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(24.dp))

                    // Botón Reservar horario (solo si hay userId)
                    if (userId != null) {
                        OutlinedButton(
                            onClick = {
                                showTimeSlots = true
                                svcVm?.loadTimeSlots(servicio.id)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !reserving,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorPrincipal),
                            border = BorderStroke(1.dp, ColorPrincipal)
                        ) {
                            if (reserving) {
                                InlineLoadingIndicator(
                                    message = "",
                                    isVisible = true,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text("Reservar horario")
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Mostrar Agregar al Carrito solamente si existe reserva para este servicio
                    val hasReserved = reservedMap[servicio.id] != null

                    Button(
                        onClick = {
                            // Si tenemos cartVm y una reserva, usarla para crear cart_detail con reservation info
                            if (cartVm != null && hasReserved) {
                                val reservedSlotId = reservedMap[servicio.id]
                                val slot = timeSlots.firstOrNull { it.id == reservedSlotId } ?: selectedSlot
                                val reservationDate = slot?.date
                                cartVm.addToCartWithReservation(servicio, userId ?: 0, reservationDate, reservedSlotId)
                                onDismiss()
                            } else {
                                onAgregarCarrito(servicio)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorPrincipal,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = hasReserved // habilitado solo después de reservar
                    ) {
                        Text("Agregar al Carrito", style = MaterialTheme.typography.bodyLarge)
                    }

                    // Mostrar resumen del slot seleccionado si existe
                    selectedSlot?.let { slot ->
                        Spacer(Modifier.height(12.dp))
                        Text("Seleccionado: ${slot.date} ${slot.start_time} - ${slot.end_time}")
                    }
                }
            }

            // Dialogo para seleccionar time slot
            if (showTimeSlots) {
                Dialog(onDismissRequest = { showTimeSlots = false }) {
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Horarios disponibles", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(12.dp))

                            if (timeSlots.isEmpty()) {
                                Text("No hay horarios disponibles")
                            } else {
                                timeSlots.forEach { slot ->
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("${slot.date} ${slot.start_time} - ${slot.end_time}")
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                // reservar
                                                svcVm?.reserveTimeSlot(servicio.id, userId ?: 0, slot.id)
                                                selectedSlot = slot
                                                showTimeSlots = false
                                            }, 
                                            enabled = !reserving,
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = ColorPrincipal
                                            ),
                                            border = BorderStroke(1.dp, ColorPrincipal)
                                        ) {
                                            Text("Seleccionar")
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { showTimeSlots = false }, 
                                modifier = Modifier.align(Alignment.End),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ColorPrincipal,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Cerrar")
                            }
                        }
                    }
                }
            }

            // Snackbar host para mostrar mensajes
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                SnackbarHost(hostState = snackbarHostState)
            }
        }
    }
}
