package com.antaedo_karfuentealba.eva3_ambientefest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.PurchaseCartItemWithReservation
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.PurchasePayment
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.PurchaseReservation
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.TimeSlot
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.CartRepository
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.ServiceRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartDetailViewModel(
    private val repo: CartRepository = CartRepository(),
    private val serviceRepo: ServiceRepository = ServiceRepository()
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _items = MutableStateFlow<List<PurchaseCartItemWithReservation>>(emptyList())
    val items: StateFlow<List<PurchaseCartItemWithReservation>> = _items

    private val _payment = MutableStateFlow<PurchasePayment?>(null)
    val payment: StateFlow<PurchasePayment?> = _payment

    fun loadCartDetailsForUser(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val cartRes = repo.getActiveCart(userId)
                val cart = cartRes.getOrNull()
                if (cart == null) {
                    _error.value = "No hay carrito activo para el usuario"
                    _items.value = emptyList()
                    _payment.value = null
                    return@launch
                }

                val cartId = cart.id

                val detailsRes = repo.getCartDetails(cartId)
                val detailsRaw = detailsRes.getOrNull() ?: emptyList()
                val details = detailsRaw.filter { it.cart_id == cartId }

                val slotIds = details.mapNotNull { it.time_slot_id }.distinct()
                val slotMap = mutableMapOf<Int, TimeSlot?>()
                if (slotIds.isNotEmpty()) {
                    val deferred = slotIds.map { id ->
                        viewModelScope.async { serviceRepo.getTimeSlotById(id).getOrNull() }
                    }
                    val results = deferred.map { it.await() }
                    slotIds.forEachIndexed { idx, id -> slotMap[id] = results[idx] }
                }

                val itemsWithRes = details.map { d ->
                    val ts = d.time_slot_id?.let { slotMap[it] }
                    val reservation = ts?.let {
                        PurchaseReservation(
                            service_id = d.service_id,
                            date = it.date,
                            start_time = it.start_time,
                            end_time = it.end_time,
                            service_name = d.service_name
                        )
                    }

                    PurchaseCartItemWithReservation(
                        service_id = d.service_id,
                        service_name = d.service_name,
                        quantity = d.quantity,
                        subtotal = d.subtotal,
                        reservation = reservation
                    )
                }
                _items.value = itemsWithRes

                val paymentsRes = repo.getPaymentsByCart(cartId)
                val payments = paymentsRes.getOrNull() ?: emptyList()
                val first = payments.firstOrNull()
                if (first != null) {
                    val paymentModel = PurchasePayment(
                        id = (first["id"] as? Number)?.toInt(),
                        total_amount = (first["total_amount"] as? Number)?.toDouble(),
                        payment_method = first["payment_method"] as? String,
                        status = first["status"] as? String,
                        cart_id = (first["cart_id"] as? Number)?.toInt(),
                        user_id = (first["user_id"] as? Number)?.toInt()
                    )
                    _payment.value = paymentModel
                } else {
                    _payment.value = null
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar detalles"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clear() {
        _items.value = emptyList()
        _payment.value = null
        _error.value = null
    }
}