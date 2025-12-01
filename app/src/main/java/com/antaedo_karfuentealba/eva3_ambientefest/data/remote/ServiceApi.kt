package com.antaedo_karfuentealba.eva3_ambientefest.data.remote

import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.XanoImage
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.TimeSlot
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.ReservationRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.ReservationResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.AvailableTimeSlotRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.requests.UpdateServiceImageRequest
import okhttp3.MultipartBody
import retrofit2.http.*

interface ServiceApi {
    @GET("service")
    suspend fun getServices(): List<Servicio>

    @Multipart
    @POST("upload/image")
    suspend fun uploadImages(
        @Part images: List<MultipartBody.Part>
    ): List<XanoImage>

    @PATCH("service/{id}")
    suspend fun updateServiceImages(
        @Path("id") id: Int,
        @Body request: UpdateServiceImageRequest
    ): Servicio

    @POST("service_time_slot/available")
    suspend fun getAvailableTimeSlots(@Body request: AvailableTimeSlotRequest): List<TimeSlot>

    // Nuevo: endpoint que filtra por service_id (GET /service_time_slot_id?service_id=..)
    @GET("service_time_slot_id")
    suspend fun getTimeSlotsByService(@Query("service_id") serviceId: Int): List<TimeSlot>

    // Fallback: algunos proyectos exponen GET /service_time_slot?service_id=..&date=..
    @GET("service_time_slot")
    suspend fun getTimeSlots(
        @Query("service_id") serviceId: Int,
        @Query("date") date: String = ""
    ): List<TimeSlot>

    @POST("service_reservation")
    suspend fun createReservation(@Body request: ReservationRequest): ReservationResponse

    // Obtener servicio por id
    @GET("service/{id}")
    suspend fun getServiceById(@Path("id") id: Int): Servicio

    // Obtener time slot por id
    @GET("service_time_slot/{id}")
    suspend fun getTimeSlotById(@Path("id") id: Int): TimeSlot
}
