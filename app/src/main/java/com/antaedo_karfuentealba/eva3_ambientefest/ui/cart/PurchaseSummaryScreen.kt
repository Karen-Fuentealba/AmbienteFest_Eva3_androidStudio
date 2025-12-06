package com.antaedo_karfuentealba.eva3_ambientefest.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.lifecycle.viewmodel.compose.viewModel
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.CartViewModel
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorFondoSeccion
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import androidx.compose.ui.graphics.Color
import com.antaedo_karfuentealba.eva3_ambientefest.ui.components.LoadingIndicator
import com.antaedo_karfuentealba.eva3_ambientefest.ui.components.InlineLoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseSummaryScreen(
    cartViewModel: CartViewModel? = null,
    onBack: () -> Unit
) {
    val vm = cartViewModel ?: viewModel<CartViewModel>()

    val summary by vm.purchaseSummary.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(success) {
        success?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearSuccess()
            onBack()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {

                Text("RESUMEN DE COMPRA", style = MaterialTheme.typography.headlineSmall, color = ColorPrincipal)
                Spacer(modifier = Modifier.height(8.dp))

                if (loading && summary == null) {
                    LoadingIndicator(
                        message = "Cargando resumen de compra...",
                        isVisible = true
                    )
                    return@Column
                }

                val currentSummary = summary
                if (currentSummary == null) {
                    Text("No se encontró información de la compra.")
                } else {
                    val p = currentSummary.payment
                    val cartItems = currentSummary.cartItems

                    if (p != null) {
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(6.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Nº de Pago: ${p.id ?: "-"}", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Método: ${p.payment_method ?: "-"}")
                                Text("Estado: Aprobado")
                                val totalAmount = (p.total_amount ?: 0.0).toInt()
                                val formattedTotal = totalAmount.toString().reversed().chunked(3).joinToString(".").reversed()
                                Text("Total Pagado: $${formattedTotal}")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Servicios comprados", style = MaterialTheme.typography.titleMedium, color = ColorPrincipal)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (cartItems.isEmpty()) {
                         Text("No hay servicios en este resumen.")
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            lazyItems(cartItems) { item ->
                                Card(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(item.service_name ?: "Servicio", style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Cantidad: ${item.quantity}")
                                        val subtotal = item.subtotal.toInt()
                                        val formattedSubtotal = subtotal.toString().reversed().chunked(3).joinToString(".").reversed()
                                        Text("Subtotal: $${formattedSubtotal}")
                                        item.reservation?.let { r ->
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("Reserva:")
                                            Text("Fecha: ${r.date ?: "-"}")
                                            Text("${r.start_time ?: "-"} - ${r.end_time ?: "-"}")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { vm.finalizarCompraYLimpiarCarrito() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrincipal, contentColor = ColorFondoSeccion)
                    ) {
                        if (loading) {
                            InlineLoadingIndicator(
                                message = "",
                                isVisible = true,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Finalizar")
                        }
                    }
                }
            }
        }
    }
}