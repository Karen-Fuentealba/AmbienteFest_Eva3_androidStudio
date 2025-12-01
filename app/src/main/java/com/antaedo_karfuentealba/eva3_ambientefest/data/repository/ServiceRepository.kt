package com.antaedo_karfuentealba.eva3_ambientefest.data.repository

import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.XanoImage
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.TimeSlot
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.ReservationRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.ReservationResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.AvailableTimeSlotRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.requests.UpdateServiceImageRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.ApiConfig
import com.antaedo_karfuentealba.eva3_ambientefest.data.remote.ServiceApi
import okhttp3.MultipartBody

class ServiceRepository(
    private val api: ServiceApi = ApiConfig.provideProjectRetrofit({ null }).create(ServiceApi::class.java)
) {

    suspend fun getServices(): Result<List<Servicio>> {
        return try {
            val res = api.getServices()
            Result.success(res)
        } catch (_: Exception) {
            Result.failure(Exception("Error al obtener servicios"))
        }
    }

    suspend fun uploadImages(parts: List<MultipartBody.Part>): Result<List<XanoImage>> {
        return try {
            val res = api.uploadImages(parts)
            Result.success(res)
        } catch (_: Exception) {
            Result.failure(Exception("Error al subir imágenes"))
        }
    }

    suspend fun updateServiceImages(id: Int, imagenes: List<XanoImage>): Result<Servicio> {
        return try {
            val request = UpdateServiceImageRequest(imagenes)
            val res = api.updateServiceImages(id, request)
            Result.success(res)
        } catch (_: Exception) {
            Result.failure(Exception("Error al actualizar servicio"))
        }
    }

    suspend fun getAvailableTimeSlots(request: AvailableTimeSlotRequest): Result<List<TimeSlot>> {
        return try {
            // Intentar POST /service_time_slot/available primero
            val res = api.getAvailableTimeSlots(request)
            Result.success(res)
        } catch (_: Exception) {
            // Si falla, intentar GET /service_time_slot_id?service_id=.. (filtrado por servicio)
            try {
                val res2 = api.getTimeSlotsByService(request.service_id)
                Result.success(res2)
            } catch (_: Exception) {
                // Si aún falla, usar el fallback GET /service_time_slot?service_id=..&date=..
                try {
                    val res3 = api.getTimeSlots(request.service_id, request.date)
                    Result.success(res3)
                } catch (_: Exception) {
                    Result.failure(Exception("Error al obtener time slots"))
                }
            }
        }
    }

    suspend fun createReservation(request: ReservationRequest): Result<ReservationResponse> {
        return try {
            val res = api.createReservation(request)
            Result.success(res)
        } catch (_: Exception) {
            Result.failure(Exception("Error al crear reserva"))
        }
    }

    suspend fun getServiceById(id: Int): Result<Servicio> {
        return try {
            val res = api.getServiceById(id)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTimeSlotById(id: Int): Result<TimeSlot> {
        return try {
            val res = api.getTimeSlotById(id)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
