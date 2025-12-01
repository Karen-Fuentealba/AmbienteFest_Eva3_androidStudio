package com.antaedo_karfuentealba.eva3_ambientefest.data.model

data class Cart(
    val id: Int,
    val active: Boolean,
    val user_id: Int
)

data class CartCreateRequest(
    val user_id: Int,
    val active: Boolean = true
)

data class CartDetailRequest(
    val cart_id: Int,
    val service_id: Int,
    val service_name: String,
    val provider: String,
    val unit_price: Double,
    val quantity: Int = 1,
    val subtotal: Double,
    val metadata: Any? = null,
    val reservation_date: String? = null,
    val time_slot_id: Int? = null
)

// La respuesta puede variar; definimos un modelo que capture campos útiles
// metadata queda como Any? y debe mapearse en el repo o UI según sea necesario
data class CartDetailResponse(
    val id: Int,
    val cart_id: Int,
    val service_id: Int,
    val service_name: String? = null,
    val provider: String? = null,
    val unit_price: Double? = null,
    val quantity: Int,
    val subtotal: Double,
    val metadata: Any? = null,
    val reservation_date: String? = null,
    val time_slot_id: Int? = null,
    val created_at: Long? = null // timestamp en ms retornado por Xano
)

// Request para registrar pagos
data class PaymentRequest(
    val cart_id: Int,
    val user_id: Int,
    val total_amount: Double,
    val payment_method: String,
    val status: String = "paid"
)
