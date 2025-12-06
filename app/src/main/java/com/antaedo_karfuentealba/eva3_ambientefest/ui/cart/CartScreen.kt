package com.antaedo_karfuentealba.eva3_ambientefest.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.XanoImage
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.CartViewModel
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.foundation.BorderStroke
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorFondoSeccion
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorContenido
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.compose.ui.unit.Dp
import com.antaedo_karfuentealba.eva3_ambientefest.ui.components.LoadingIndicator
import com.antaedo_karfuentealba.eva3_ambientefest.ui.components.InlineLoadingIndicator

data class CartItemUiModel(
    val id: Int,
    val name: String,
    val provider: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double,
    val imagen: List<XanoImage> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    items: List<CartItemUiModel>,
    cartId: Int,
    userId: Int,
    onBack: () -> Unit = {}
) {
    val metodoPago by viewModel.metodoPago
    val mensajeError by viewModel.mensajeError
    val loading by viewModel.loading.collectAsState()
    val success by viewModel.success.collectAsState()
    val error by viewModel.error.collectAsState()

    val ivaRate = 0.19
    val totalBeforeIva by remember(items) { derivedStateOf { items.sumOf { it.unitPrice * it.quantity } } }
    val totalWithIva by remember(totalBeforeIva) { derivedStateOf { totalBeforeIva * (1 + ivaRate) } }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(success) {
        success?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSuccess()
        }
    }

    LaunchedEffect(mensajeError) {
        mensajeError?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMensajeError()
        }
    }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = ColorFondoSeccion,
        topBar = {
            TopAppBar(
                title = { Text("Carrito", color = ColorPrincipal) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorFondoSeccion)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorFondoSeccion)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (items.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("El carrito está vacío", color = ColorContenido)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(items, key = { it.id }) { item ->
                            CartItemCard(item = item, onRemove = { viewModel.removeItemOptimistic(item.id) })
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = ColorPrincipal, thickness = 1.dp)
                    Spacer(Modifier.height(12.dp))

                    // Resumen de compra
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val totalSinIva = totalBeforeIva.toInt().toString().reversed().chunked(3).joinToString(".").reversed()
                        val iva = (totalBeforeIva * ivaRate).toInt().toString().reversed().chunked(3).joinToString(".").reversed()
                        val totalConIva = totalWithIva.toInt().toString().reversed().chunked(3).joinToString(".").reversed()

                        Text("Total (sin IVA): $${totalSinIva}", color = ColorContenido)
                        Text("IVA (19%): $${iva}", color = ColorContenido)
                        Text("Total con IVA: $${totalConIva}", style = MaterialTheme.typography.titleLarge, color = ColorPrincipal)

                        Spacer(Modifier.height(12.dp))

                        Text("Método de pago", color = ColorPrincipal)
                        Spacer(Modifier.height(6.dp))
                        Row {
                            listOf("transferencia", "tarjeta").forEach { metodo ->
                                val selected = metodoPago == metodo
                                if (selected) {
                                    Button(
                                        onClick = { viewModel.setMetodoPago(metodo) },
                                        modifier = Modifier.padding(end = 8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrincipal, contentColor = ColorFondoSeccion)
                                    ) {
                                        Text(metodo)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { viewModel.setMetodoPago(metodo) },
                                        modifier = Modifier.padding(end = 8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorPrincipal),
                                        border = BorderStroke(1.dp, ColorPrincipal)
                                    ) {
                                        Text(metodo)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (items.isEmpty()) {
                                    viewModel.mensajeError.value = "No puedes confirmar una compra con el carrito vacío"
                                    return@Button
                                }
                                viewModel.confirmarCompra(cartId, userId, totalWithIva)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loading,
                            colors = ButtonDefaults.buttonColors(containerColor = ColorPrincipal, contentColor = ColorFondoSeccion)
                        ) {
                            if (loading) {
                                InlineLoadingIndicator(
                                    message = "",
                                    isVisible = true,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text("Finalizar compra")
                            }
                        }
                    }
                }
            }
            
            // Indicador de carga superpuesto para operaciones del carrito
            if (loading) {
                LoadingIndicator(
                    message = "Procesando compra...",
                    isVisible = true
                )
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItemUiModel,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .background(ColorFondoSeccion)
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imgPath = item.imagen.firstOrNull()?.path
            val rawUrl = imgPath?.let {
                try {
                    val base = java.net.URI("https://x8ki-letl-twmt.n7.xano.io")
                    val resolved = base.resolve(it).normalize()
                    resolved.toString()
                } catch (ex: Exception) {
                    try { 
                        "https://x8ki-letl-twmt.n7.xano.io${it.replace(" ", "%20")}" 
                    } catch (_: Exception) { 
                        null 
                    }
                }
            }

            if (rawUrl != null) {
                SubcomposeAsyncImage(
                    model = rawUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                ) {
                    when (painter.state) {
                        is coil.compose.AsyncImagePainter.State.Loading -> {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is coil.compose.AsyncImagePainter.State.Error -> {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.BrokenImage, contentDescription = "Sin imagen", tint = Color.Gray)
                            }
                        }
                        else -> SubcomposeAsyncImageContent()
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.BrokenImage, contentDescription = "Sin imagen", tint = Color.Gray)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium, color = ColorPrincipal)
                Text("Proveedor: ${item.provider}", color = ColorContenido)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Cantidad: ${item.quantity}", color = ColorContenido)
                    Spacer(Modifier.width(16.dp))
                    val formattedSubtotal = item.subtotal.toInt().toString().reversed().chunked(3).joinToString(".").reversed()
                    Text("Subtotal: $${formattedSubtotal}", color = ColorContenido)
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ColorPrincipal)
            }
        }
    }
}
