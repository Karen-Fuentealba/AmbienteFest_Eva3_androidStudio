package com.antaedo_karfuentealba.eva3_ambientefest.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import java.util.Locale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import com.antaedo_karfuentealba.eva3_ambientefest.ui.components.ValoracionEstrellas
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorContenido

@Composable
fun ServicioCard(
    servicio: Servicio,
    onAgregarCarrito: (Servicio) -> Unit,
    onVerDetalle: (Servicio) -> Unit = {},
    showAddButton: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            // Carousel de múltiples imágenes
            if (servicio.imagen.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(servicio.imagen) { imagen ->
                        AsyncImage(
                            model = "https://x8ki-letl-twmt.n7.xano.io${imagen.path}",
                            contentDescription = servicio.name,
                            modifier = Modifier
                                .width(160.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = "Sin imágenes",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Column(Modifier.padding(16.dp)) {
                Text(
                    servicio.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        color = ColorPrincipal
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Proveedor: ${servicio.provider}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif,
                        color = Color.Black
                    )
                )
                Spacer(Modifier.height(4.dp))
                val disponibilidad = servicio.availability ?: if (servicio.available == true) "Disponible" else "No disponible"
                Text(
                    "Disponibilidad: $disponibilidad",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ValoracionEstrellas(
                        valoracion = servicio.rating ?: 0.0
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("(${servicio.num_ratings ?: 0})", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    
                    val price = servicio.price ?: 0.0
                    val intPrice = price.toInt()
                    val formattedPrice = "$ ${intPrice.toString().reversed().chunked(3).joinToString(".").reversed()}"

                    Text(
                        text = formattedPrice,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 18.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { onVerDetalle(servicio) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ColorContenido
                        ),
                        border = BorderStroke(1.dp, ColorContenido)
                    ) {
                        Text("Ver Detalle")
                    }

                    Spacer(Modifier.width(8.dp))

                    if (showAddButton) {
                        Button(
                            onClick = { onAgregarCarrito(servicio) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorPrincipal,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Agregar")
                        }
                    }
                }
            }
        }
    }
}
