package com.antaedo_karfuentealba.eva3_ambientefest.ui.services

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.antaedo_karfuentealba.eva3_ambientefest.data.local.SessionDataStore
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.ServiceRepository
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.CartViewModel
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.ServiceViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun ServicesRoute(
    viewModel: ServiceViewModel? = null,
    cartViewModel: CartViewModel? = null,
    onAgregarCarrito: (com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio) -> Unit = {},
    onLogout: () -> Unit = {},
    onOpenCart: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionStore = remember { SessionDataStore(context) }
    val session = sessionStore.sessionFlow.collectAsState(initial = null)
    val userId = session.value?.userId
    val isLoggedIn = userId != null

    // si no se pasó un viewModel, crear uno local
    val svcVm = viewModel ?: viewModel(factory = ServiceViewModel.Factory(ServiceRepository()))

    // Crear CartViewModel con token provider
    val cartVm = cartViewModel ?: remember {
        CartViewModel(tokenProvider = { session.value?.token })
    }

    // Cargar servicios automáticamente y asegurarse de sincronizar el carrito al entrar
    LaunchedEffect(Unit) {
        svcVm.loadServices()
        userId?.let { id ->
            // Forzar sincronización del carrito cuando se entra a la pantalla de servicios
            cartVm.loadCartForUser(id)
        }
    }

    // Cargar/crear carrito cuando haya userId (asegura que entrando desde Splash o Signup se inicialice)
    LaunchedEffect(userId) {
        userId?.let { id ->
            cartVm.loadCartForUser(id)
        }
    }

    // Handler para agregar al carrito usando CartViewModel
    val addHandler: (com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio) -> Unit = { servicio ->
        userId?.let { id ->
            // Asegurarse de que el carrito esté sincronizado antes de agregar
            cartVm.loadCartForUser(id)
            cartVm.addToCart(servicio, id)
        }
        // También ejecutar el callback externo si se proporciona
        onAgregarCarrito(servicio)
    }

    ServicesScreen(
        servicesState = svcVm.services,
        loadingState = svcVm.loading,
        errorState = svcVm.error,
        onRefresh = { svcVm.loadServices() },
        onAgregarCarrito = addHandler,
        cartViewModel = cartVm,
        onLogout = onLogout,
        isLoggedIn = isLoggedIn,
        onOpenCart = onOpenCart,
        onNavigateToLogin = onNavigateToLogin,
        serviceViewModel = svcVm,
        userId = userId
    )
}
