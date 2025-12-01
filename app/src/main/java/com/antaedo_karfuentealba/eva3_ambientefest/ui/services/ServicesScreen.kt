package com.antaedo_karfuentealba.eva3_ambientefest.ui.services

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.CartViewModel
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import androidx.lifecycle.viewmodel.compose.viewModel
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.ServiceViewModel
import com.antaedo_karfuentealba.eva3_ambientefest.ui.components.LoadingIndicator
import com.antaedo_karfuentealba.eva3_ambientefest.ui.components.CartIconWithBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    servicesState: kotlinx.coroutines.flow.StateFlow<List<Servicio>>,
    loadingState: kotlinx.coroutines.flow.StateFlow<Boolean>,
    errorState: kotlinx.coroutines.flow.StateFlow<String?>,
    onRefresh: () -> Unit,
    onAgregarCarrito: (Servicio) -> Unit,
    cartViewModel: CartViewModel = viewModel(),
    onLogout: () -> Unit = {},
    isLoggedIn: Boolean = false,
    onOpenCart: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    serviceViewModel: ServiceViewModel? = null,
    userId: Int? = null
) {
    val services = servicesState.collectAsState()
    val loading = loadingState.collectAsState()
    val error = errorState.collectAsState()

    val cartLoading by cartViewModel.loading.collectAsState()
    val cartSuccess by cartViewModel.success.collectAsState()
    val cartError by cartViewModel.error.collectAsState()
    val cartDetails by cartViewModel.cartDetails.collectAsState()
    val cartItemCount = cartDetails.sumOf { it.quantity }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(cartSuccess) {
        cartSuccess?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            // opcional: podrías limpiar el estado en el ViewModel si lo implementas
        }
    }

    LaunchedEffect(cartError) {
        cartError?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Inicializar el carrito cuando el usuario esté logueado
    LaunchedEffect(isLoggedIn, userId) {
        if (isLoggedIn && userId != null) {
            cartViewModel.initializeCart(userId)
        }
    }

    // Estado para detalle modal
    var selectedServicio by remember { mutableStateOf<Servicio?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Servicios",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FontFamily.Serif,
                            color = ColorPrincipal
                        )
                    )
                },
                actions = {
                    if (isLoggedIn) {
                        CartIconWithBadge(
                            cartCount = cartItemCount,
                            onClick = onOpenCart
                        )
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
                        }
                    } else {
                        TextButton(onClick = onNavigateToLogin) {
                            Text("Iniciar sesión", color = Color.Black)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                loading.value -> {
                    LoadingIndicator(
                        message = "Cargando servicios...",
                        isVisible = true
                    )
                }
                services.value.isEmpty() -> {
                    // Estado de lista vacía (sin mostrar errores de carga)
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No hay servicios disponibles")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRefresh) { Text("Recargar") }
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
                        items(services.value) { servicio ->
                            ServicioCard(
                                servicio = servicio,
                                onAgregarCarrito = onAgregarCarrito,
                                onVerDetalle = { selectedServicio = it },
                                showAddButton = false
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Overlay loading when adding to cart
            if (cartLoading) {
                LoadingIndicator(
                    message = "Agregando al carrito...",
                    isVisible = true
                )
            }

            // Detalle modal
            selectedServicio?.let { servicio ->
                ServicioDetalleScreen(
                    servicio = servicio,
                    onDismiss = { selectedServicio = null },
                    onAgregarCarrito = { s ->
                        onAgregarCarrito(s)
                        selectedServicio = null
                    },
                    serviceViewModel = serviceViewModel,
                    cartViewModel = cartViewModel,
                    userId = userId
                )
            }
        }
    }
}
