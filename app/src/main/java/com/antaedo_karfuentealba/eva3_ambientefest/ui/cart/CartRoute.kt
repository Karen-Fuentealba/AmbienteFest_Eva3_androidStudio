package com.antaedo_karfuentealba.eva3_ambientefest.ui.cart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.antaedo_karfuentealba.eva3_ambientefest.data.local.SessionDataStore
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.XanoImage
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.CartViewModel
import androidx.compose.ui.platform.LocalContext
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.ServiceRepository
import androidx.compose.runtime.mutableStateListOf

@Composable
fun CartRoute(
    navController: NavController,
    viewModel: CartViewModel? = null
) {
    val context = LocalContext.current
    val sessionStore = remember { SessionDataStore(context) }
    val session by sessionStore.sessionFlow.collectAsState(initial = null)
    val userId = session?.userId

    // Crear CartViewModel con token provider
    val cartVm = viewModel ?: remember {
        CartViewModel(tokenProvider = { session?.token })
    }

    // Crear repo de servicios para fallback de imagenes
    val serviceRepo = remember { ServiceRepository() }

    // Cargar datos cuando exista userId
    LaunchedEffect(userId) {
        userId?.let {
            cartVm.loadCartForUser(it)
        }
    }

    // Observar cambios dinámicamente
    val cartDetails by cartVm.cartDetails.collectAsState()
    val activeCartId by cartVm.activeCartId.collectAsState()
    val purchaseSummary by cartVm.purchaseSummary.collectAsState()

    // Si hay resumen de compra, navegar a la pantalla de resumen
    LaunchedEffect(purchaseSummary) {
        if (purchaseSummary != null) {
            navController.navigate("purchase_summary") {
                launchSingleTop = true
            }
        }
    }

    // Mapear CartDetailResponse a CartItemUiModel dinámicamente
    val initialItems = cartDetails.map { detail ->
        val imagenList: List<XanoImage> = when (val meta = detail.metadata) {
            is Map<*, *> -> {
                val imagenAny = meta["imagen"]
                if (imagenAny is List<*>) {
                    imagenAny.mapNotNull { itMap ->
                        if (itMap is Map<*, *>) {
                            val path = itMap["path"] as? String
                            val name = itMap["name"] as? String
                            val type = itMap["type"] as? String
                            val size = (itMap["size"] as? Number)?.toInt()
                            val mime = itMap["mime"] as? String
                            val metaMap = itMap["meta"] as? Map<*, *>
                            val width = (metaMap?.get("width") as? Number)?.toInt()
                            val height = (metaMap?.get("height") as? Number)?.toInt()
                            XanoImage(
                                path = path,
                                name = name,
                                type = type,
                                size = size,
                                mime = mime,
                                meta = if (width != null || height != null)
                                    com.antaedo_karfuentealba.eva3_ambientefest.data.model.ImageMeta(width = width, height = height)
                                else null
                            )
                        } else null
                    }
                } else emptyList()
            }
            else -> emptyList()
        }

        CartItemUiModel(
            id = detail.id,
            name = detail.service_name ?: "Servicio",
            provider = detail.provider ?: "",
            unitPrice = detail.unit_price ?: (detail.subtotal / detail.quantity.toDouble()),
            quantity = detail.quantity,
            subtotal = detail.subtotal,
            imagen = imagenList
        )
    }

    // Mantener una lista mutable para poder actualizar items cuando se obtienen imagenes desde service
    val itemsState = remember { mutableStateListOf<CartItemUiModel>() }
    LaunchedEffect(cartDetails) {
        itemsState.clear()
        itemsState.addAll(initialItems)

        // Para cada item sin imagen en metadata, intentar obtener la imagen desde el servicio original
        for ((index, detail) in cartDetails.withIndex()) {
            val hasImage = itemsState.getOrNull(index)?.imagen?.isNotEmpty() == true
            if (!hasImage) {
                // Llamada suspend para obtener servicio
                val serviceRes = serviceRepo.getServiceById(detail.service_id).getOrNull()
                val fallbackImages = serviceRes?.imagen ?: emptyList()
                if (fallbackImages.isNotEmpty()) {
                    // actualizar item con imagenes del servicio
                    val current = itemsState.getOrNull(index) ?: continue
                    itemsState[index] = current.copy(imagen = fallbackImages)
                }
            }
        }
    }

    // Mostrar la pantalla con datos actualizados dinámicamente
    CartScreen(
        viewModel = cartVm,
        items = itemsState,
        cartId = activeCartId ?: 0,
        userId = userId ?: 0,
        onBack = {
            // Navegar explícitamente a home_user para forzar recreación y recarga
            navController.navigate("home_user") {
                popUpTo("home_user") { inclusive = true }
            }
        }
    )
}
