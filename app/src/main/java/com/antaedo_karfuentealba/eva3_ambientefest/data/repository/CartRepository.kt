package com.antaedo_karfuentealba.eva3_ambientefest.data.repository

import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Cart
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.CartCreateRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.CartDetailRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.CartDetailResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.PaymentRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.ApiConfig
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.CartApiService
import retrofit2.HttpException

class CartRepository(
    private val tokenProvider: () -> String? = { null },
    private val api: CartApiService = ApiConfig.provideProjectRetrofit(tokenProvider).create(CartApiService::class.java)
) {

    suspend fun getActiveCart(userId: Int): Result<Cart?> {
        return try {
            val list = api.getCartActive(userId)
            val active = list.firstOrNull { it.active }
            Result.success(active)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCart(userId: Int): Result<Cart> {
        return try {
            val res = api.createCart(CartCreateRequest(user_id = userId, active = true))
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addServiceToCart(request: CartDetailRequest): Result<CartDetailResponse> {
        return try {
            val res = api.addServiceToCart(request)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCartDetails(cartId: Int): Result<List<CartDetailResponse>> {
        return try {
            val res = api.getCartDetails(cartId)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCartItem(id: Int): Result<Unit> {
        return try {
            api.deleteCartDetail(id)
            Result.success(Unit)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                // Si ya no existe, es un éxito desde el punto de vista del cliente
                return Result.success(Unit)
            }
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarPago(request: PaymentRequest): Result<Map<String, Any>> {
        return try {
            val res = api.registrarPago(request)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener pagos asociados a un carrito
    suspend fun getPaymentsByCart(cartId: Int, sort: String? = null, limit: Int? = null): Result<List<Map<String, Any>>> {
        return try {
            val res = api.getPaymentsByCart(cartId, sort, limit)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Vacía los items del carrito indicando el cartId (usa el endpoint clear_cart que espera { cart_id }).
    // Si clear_cart falla (400/404), hace fallback borrando cada cart_detail individualmente.
    suspend fun vaciarCarrito(cartId: Int): Result<Unit> {
        try {
            val body = mapOf("cart_id" to cartId)
            api.vaciarCarrito(body)
            return Result.success(Unit)
        } catch (e: HttpException) {
            // Si el error es 400 o 404, intentamos fallback
            if (e.code() == 400 || e.code() == 404) {
                return try {
                    // Obtener detalles del carrito y eliminar item por item
                    val detailsRes = api.getCartDetails(cartId)
                    detailsRes.forEach { detail ->
                        try {
                            api.deleteCartDetail(detail.id)
                        } catch (de: Exception) {
                            // ignorar fallos por item
                        }
                    }
                    // Intentar además desactivar el carrito para mantener consistencia
                    try {
                        api.updateCart(cartId, mapOf("active" to false))
                    } catch (ue: Exception) {
                        // ignorar
                    }
                    Result.success(Unit)
                } catch (inner: Exception) {
                    return Result.failure(inner)
                }
            }
            return Result.failure(e)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    // Desactiva un carrito (se usa después de que la compra se completa y el carrito se vacía)
    suspend fun deactivateCart(cartId: Int): Result<Cart> {
        return try {
            val res = api.updateCart(cartId, mapOf("active" to false))
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}