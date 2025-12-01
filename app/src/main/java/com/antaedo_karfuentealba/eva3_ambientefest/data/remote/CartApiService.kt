package com.antaedo_karfuentealba.eva3_ambientefest.data.remote

import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Cart
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.CartCreateRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.CartDetailRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.CartDetailResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.PaymentRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.HTTP

interface CartApiService {
    @GET("cart")
    suspend fun getCartActive(@Query("user_id") userId: Int): List<Cart>

    @POST("cart")
    suspend fun createCart(@Body body: CartCreateRequest): Cart

    @PATCH("cart/{id}")
    suspend fun updateCart(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any>): Cart

    @POST("cart_detail")
    suspend fun addServiceToCart(@Body body: CartDetailRequest): CartDetailResponse

    @GET("cart_detail")
    suspend fun getCartDetails(@Query("cart_id") cartId: Int): List<CartDetailResponse>

    @GET("cart_detail/{id}")
    suspend fun getCartDetailById(@Path("id") id: Int): CartDetailResponse

    @DELETE("cart_detail/{id}")
    suspend fun deleteCartDetail(@Path("id") id: Int): Map<String, Any>

    @POST("payment")
    suspend fun registrarPago(@Body body: PaymentRequest): Map<String, Any>

    // Obtener pagos filtrando por user_id y permitiendo orden/limit
    @GET("payment")
    suspend fun getPaymentsByUser(@Query("user_id") userId: Int, @Query("sort") sort: String? = null, @Query("limit") limit: Int? = null): List<Map<String, Any>>

    // Obtener pagos filtrando por cart_id
    @GET("payment")
    suspend fun getPaymentsByCart(@Query("cart_id") cartId: Int, @Query("sort") sort: String? = null, @Query("limit") limit: Int? = null): List<Map<String, Any>>

    // Obtener carrito por id
    @GET("cart/{id}")
    suspend fun getCartById(@Path("id") id: Int): Cart

    // Obtener reservas por usuario (tabla service_reservation)
    @GET("service_reservation")
    suspend fun getReservationsByUser(@Query("user_id") userId: Int): List<Map<String, Any>>

    // Obtener reservas filtradas por service_id y user_id
    @GET("service_reservation")
    suspend fun getReservationsByServiceAndUser(@Query("service_id") serviceId: Int, @Query("user_id") userId: Int): List<Map<String, Any>>

    // Reemplazamos el endpoint que daba 404 por el endpoint existente `clear_cart`.
    // Xano espera un input { int cart_id } — implementamos DELETE con body usando @HTTP
    @HTTP(method = "DELETE", path = "clear_cart", hasBody = true)
    suspend fun vaciarCarrito(@Body body: Map<String, Int>): Map<String, Any>
}
