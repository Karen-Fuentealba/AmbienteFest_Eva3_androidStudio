package com.antaedo_karfuentealba.eva3_ambientefest.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.CartDetailRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.CartDetailResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.PaymentRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.PurchaseReservation
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.PurchaseCartItemWithReservation
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.CartRepository
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import kotlinx.coroutines.flow.Flow
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Session
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.PurchasePayment
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.PurchaseSummary

class CartViewModel(
    private val tokenProvider: () -> String? = { null },
    private val repo: CartRepository = CartRepository(tokenProvider)
) : ViewModel() {

    private val serviceRepo: ServiceRepository = ServiceRepository()
    private val cartMutex = Mutex()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _cartDetails = MutableStateFlow<List<CartDetailResponse>>(emptyList())
    val cartDetails: StateFlow<List<CartDetailResponse>> = _cartDetails

    private val _activeCartId = MutableStateFlow<Int?>(null)
    val activeCartId: StateFlow<Int?> = _activeCartId

    private val _purchaseSummary = MutableStateFlow<PurchaseSummary?>(null)
    val purchaseSummary: StateFlow<PurchaseSummary?> = _purchaseSummary

    var metodoPago = mutableStateOf("tarjeta")
    var estadoCompra = mutableStateOf(false)
    var mensajeError = mutableStateOf<String?>(null)

    fun setMetodoPago(valor: String) {
        metodoPago.value = valor
    }

    fun confirmarCompra(cartId: Int, userId: Int, total: Double) {
        viewModelScope.launch {
            estadoCompra.value = true
            mensajeError.value = null
            _loading.value = true

            try {
                cartMutex.withLock {
                    val cartToUse = _activeCartId.value ?: cartId
                    val detailsList = _cartDetails.value // Usar el estado local del carrito

                    if (detailsList.isEmpty()) {
                        mensajeError.value = "No hay items en el carrito."
                        _loading.value = false
                        return@withLock
                    }

                    val pagoReq = PaymentRequest(
                        cart_id = cartToUse,
                        user_id = userId,
                        total_amount = total,
                        payment_method = metodoPago.value,
                        status = "aprobado"
                    )

                    val pagoRes = repo.registrarPago(pagoReq)
                    if (pagoRes.isFailure) {
                        mensajeError.value = pagoRes.exceptionOrNull()?.message ?: "Error registrando pago"
                        _loading.value = false
                        return@withLock
                    }

                    val paymentMap = pagoRes.getOrNull()!!
                    val paymentId = (paymentMap["id"] as? Number)?.toInt()
                    val returnedCartId = (paymentMap["cart_id"] as? Number)?.toInt() ?: cartToUse

                    val cartItemsWithRes = detailsList.map { d ->
                        var reservationForItem: PurchaseReservation? = null
                        val tsId = d.time_slot_id
                        if (tsId != null) {
                            val timeSlotObj = serviceRepo.getTimeSlotById(tsId).getOrNull()
                            reservationForItem = PurchaseReservation(
                                service_id = d.service_id,
                                date = d.reservation_date ?: timeSlotObj?.date,
                                start_time = timeSlotObj?.start_time,
                                end_time = timeSlotObj?.end_time,
                                service_name = d.service_name
                            )
                        }
                        PurchaseCartItemWithReservation(
                            service_id = d.service_id,
                            service_name = d.service_name,
                            quantity = d.quantity,
                            subtotal = d.subtotal,
                            reservation = reservationForItem
                        )
                    }

                    val paymentModel = PurchasePayment(
                        id = paymentId,
                        total_amount = (paymentMap["total_amount"] as? Number)?.toDouble(),
                        payment_method = paymentMap["payment_method"] as? String,
                        status = "Aprobado",
                        cart_id = returnedCartId,
                        user_id = userId,
                        created_at = (paymentMap["created_at"] as? Number)?.toLong()
                    )

                    _purchaseSummary.value = PurchaseSummary(
                        payment = paymentModel,
                        cartItems = cartItemsWithRes
                    )

                    _cartDetails.value = emptyList()
                }
            } catch (t: Throwable) {
                mensajeError.value = t.message ?: "Error inesperado"
                Log.e("CartViewModel", "confirmarCompra error", t)
            } finally {
                _loading.value = false
            }
        }
    }


    fun finalizarCompraYLimpiarCarrito() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _success.value = null

            val cartId = _purchaseSummary.value?.payment?.cart_id
            val userId = _purchaseSummary.value?.payment?.user_id

            if (cartId == null || userId == null) {
                _error.value = "Error: No se pudo obtener el carrito o usuario para limpiar el carrito."
                _loading.value = false
                return@launch
            }

            try {
                _cartDetails.value = emptyList()
                _activeCartId.value = null

                val clearResult = repo.vaciarCarrito(cartId)
                if (clearResult.isFailure) {
                    val ex = clearResult.exceptionOrNull()
                    Log.e("CartViewModel", "Error al vaciar carrito $cartId: ${ex?.message}")
                }

                repo.deactivateCart(cartId).onFailure {
                    Log.e("CartViewModel", "No se pudo desactivar carrito $cartId: ${it.message}")
                }

                _purchaseSummary.value = null
                _success.value = "Compra finalizada con éxito. ¡Tu carrito está listo!"

                loadCartForUser(userId, forceCreateNew = true)

            } catch (e: HttpException) {
                Log.e("CartViewModel", "Error al finalizar y limpiar el carrito para el carrito $cartId", e)
                _error.value = "Hubo un error al limpiar tu carrito. Por favor, intenta más tarde."
                _cartDetails.value = emptyList()
                _activeCartId.value = null
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error al finalizar y limpiar el carrito para el carrito $cartId", e)
                _error.value = "Hubo un error al limpiar tu carrito. Por favor, reinicia la sesión."
                _cartDetails.value = emptyList()
                _activeCartId.value = null
            } finally {
                _loading.value = false
            }
        }
    }

    fun attachSessionFlow(sessionFlow: Flow<Session?>) {
        viewModelScope.launch {
            sessionFlow.collect { session ->
                if (session?.userId != null) {
                    loadCartForUser(session.userId)
                } else {
                    clearOnLogout()
                }
            }
        }
    }

    fun clearOnLogout() {
        viewModelScope.launch {
            _cartDetails.value = emptyList()
            _activeCartId.value = null
            _purchaseSummary.value = null
            _success.value = null
            _error.value = null
            metodoPago.value = "tarjeta"
            estadoCompra.value = false
            mensajeError.value = null
        }
    }

    fun addToCartWithReservation(servicio: Servicio, userId: Int, reservationDate: String?, timeSlotId: Int?) {
        viewModelScope.launch {
            cartMutex.withLock {
                _loading.value = true
                try {
                    val cartId = _activeCartId.value ?: repo.getActiveCart(userId).getOrNull()?.id ?: repo.createCart(userId).getOrThrow().id
                    _activeCartId.value = cartId
                    val request = CartDetailRequest(
                        cart_id = cartId,
                        service_id = servicio.id,
                        service_name = servicio.name,
                        provider = servicio.provider,
                        unit_price = servicio.price,
                        quantity = 1,
                        subtotal = servicio.price,
                        reservation_date = reservationDate,
                        time_slot_id = timeSlotId
                    )
                    repo.addServiceToCart(request).onSuccess {
                        loadCartDetails(cartId)
                        _success.value = "Agregado al carrito"
                    }.onFailure { 
                        _error.value = it.message ?: "Error al agregar"
                    }
                } catch (e: Exception) {
                    _error.value = e.message ?: "Error inesperado"
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    fun loadCartForUser(userId: Int, forceCreateNew: Boolean = false) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val cart = if (forceCreateNew) {
                    repo.createCart(userId).getOrThrow()
                } else {
                    repo.getActiveCart(userId).getOrNull() ?: repo.createCart(userId).getOrThrow()
                }
                _activeCartId.value = cart.id
                loadCartDetails(cart.id)
            } catch (e: Exception) {
                _error.value = "Error al cargar o crear el carrito"
            } finally {
                _loading.value = false
            }
        }
    }
    
    private fun loadCartDetails(cartId: Int) {
        viewModelScope.launch {
             val detailsRes = repo.getCartDetails(cartId)
             detailsRes.onSuccess { list ->
                val filteredList = list.filter { it.cart_id == cartId }
                _cartDetails.value = filteredList
                Log.d("CartViewModel", "Cart details updated: ${filteredList.size} items, total quantity: ${filteredList.sumOf { it.quantity }}")
             }.onFailure { e ->
                Log.e("CartViewModel", "Error loading cart details: ${e.message}")
                _error.value = e.message ?: "Error al obtener detalles"
             }
        }
    }

    fun addToCart(servicio: Servicio, userId: Int) {
        viewModelScope.launch {
            cartMutex.withLock {
                _loading.value = true
                try {
                    val cartId = _activeCartId.value ?: repo.getActiveCart(userId).getOrNull()?.id ?: repo.createCart(userId).getOrThrow().id
                    _activeCartId.value = cartId
                    Log.d("CartViewModel", "Adding service ${servicio.name} to cart $cartId")
                    val request = CartDetailRequest(
                        cart_id = cartId,
                        service_id = servicio.id,
                        service_name = servicio.name,
                        provider = servicio.provider,
                        unit_price = servicio.price,
                        quantity = 1,
                        subtotal = servicio.price
                    )
                    repo.addServiceToCart(request).onSuccess {
                        Log.d("CartViewModel", "Service added successfully, reloading cart details")
                        loadCartDetails(cartId)
                        _success.value = "Agregado al carrito"
                    }.onFailure { 
                        Log.e("CartViewModel", "Failed to add service: ${it.message}")
                        _error.value = it.message ?: "Error al agregar"
                    }
                } catch (e: Exception) {
                    Log.e("CartViewModel", "Exception in addToCart: ${e.message}")
                    _error.value = e.message ?: "Error inesperado"
                } finally {
                    _loading.value = false
                }
            }
        }
    }

    fun removeItemOptimistic(cartDetailId: Int) {
        viewModelScope.launch {
            val snapshot = _cartDetails.value
            _cartDetails.value = snapshot.filter { it.id != cartDetailId }
            repo.deleteCartItem(cartDetailId).onFailure {
                _cartDetails.value = snapshot
                _error.value = "Error al eliminar el item"
            }
        }
    }

    fun clearPurchaseSummary() {
        _purchaseSummary.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun clearMensajeError() {
        mensajeError.value = null
    }

    fun refreshCartDetails(userId: Int) {
        viewModelScope.launch {
            cartMutex.withLock {
                try {
                    val cartId = _activeCartId.value ?: repo.getActiveCart(userId).getOrNull()?.id
                    cartId?.let { 
                        _activeCartId.value = it
                        loadCartDetails(it) 
                    }
                } catch (e: Exception) {
                    _error.value = "Error al cargar el carrito"
                }
            }
        }
    }

    fun initializeCart(userId: Int) {
        refreshCartDetails(userId)
    }
}
