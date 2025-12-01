package com.antaedo_karfuentealba.eva3_ambientefest.data.model

// Define el resumen completo de una compra, que incluye el pago y los items asociados.
data class PurchaseSummary(
    val payment: PurchasePayment?,
    val cartItems: List<PurchaseCartItemWithReservation>
)

// Representa un item individual dentro del resumen de compra, con su reserva asociada.
data class PurchaseCartItemWithReservation(
    val service_id: Int,
    val service_name: String?,
    val quantity: Int,
    val subtotal: Double,
    val reservation: PurchaseReservation?
)

// Información detallada sobre el pago realizado.
data class PurchasePayment(
    val id: Int?,
    val cart_id: Int?,
    val user_id: Int?,
    val total_amount: Double?,
    val payment_method: String?,
    val status: String?,
    val created_at: Long? = null
)

// Detalles de la reserva para un servicio.
data class PurchaseReservation(
    val service_id: Int,
    val date: String?,
    val start_time: String?,
    val end_time: String?,
    val service_name: String?
)
