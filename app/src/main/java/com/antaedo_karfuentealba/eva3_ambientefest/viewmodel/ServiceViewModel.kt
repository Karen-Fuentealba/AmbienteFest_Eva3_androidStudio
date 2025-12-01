package com.antaedo_karfuentealba.eva3_ambientefest.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.XanoImage
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.TimeSlot
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.ReservationRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.ReservationResponse
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServiceViewModel(private val repo: ServiceRepository) : ViewModel() {

    private val _services = MutableStateFlow<List<Servicio>>(emptyList())
    val services: StateFlow<List<Servicio>> = _services

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Estados para subida de imágenes
    private val _uploading = MutableStateFlow(false)
    val uploading: StateFlow<Boolean> = _uploading

    private val _uploadError = MutableStateFlow<String?>(null)
    val uploadError: StateFlow<String?> = _uploadError

    private val _uploadResult = MutableStateFlow<List<XanoImage>?>(null)
    val uploadResult: StateFlow<List<XanoImage>?> = _uploadResult

    // Nuevos estados para reservas y time slots
    private val _timeSlots = MutableStateFlow<List<TimeSlot>>(emptyList())
    val timeSlots: StateFlow<List<TimeSlot>> = _timeSlots

    private val _reserving = MutableStateFlow(false)
    val reserving: StateFlow<Boolean> = _reserving

    private val _reservationResult = MutableStateFlow<ReservationResponse?>(null)
    val reservationResult: StateFlow<ReservationResponse?> = _reservationResult

    // Guardar los timeSlot reservados por servicio (serviceId -> reservedTimeSlotId)
    private val _reservedMap = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val reservedMap: StateFlow<Map<Int, Int>> = _reservedMap

    fun loadServices() {
        viewModelScope.launch {
            Log.d("ServiceViewModel", "Iniciando carga de servicios...")
            _loading.value = true
            _error.value = null

            val res = repo.getServices()
            res.onSuccess { list ->
                Log.d("ServiceViewModel", "Servicios cargados exitosamente: ${list.size}")
                list.forEach { servicio ->
                    Log.d("ServiceViewModel", "Servicio: ${servicio.name} - Imágenes: ${servicio.imagen.size}")
                }
                _services.value = list
                _error.value = null
            }.onFailure { e ->
                Log.e("ServiceViewModel", "Error al cargar servicios: ${e.message}", e)
                _error.value = e.message ?: "Error al cargar servicios"
            }
            _loading.value = false
        }
    }

    fun loadTimeSlots(serviceId: Int, date: String = "") {
        viewModelScope.launch {
            _reserving.value = false
            _error.value = null
            try {
                val req = com.antaedo_karfuentealba.eva3_ambientefest.data.model.AvailableTimeSlotRequest(
                    service_id = serviceId,
                    date = date,
                    start_time = "",
                    end_time = "",
                    created_by_ = 0,
                    is_booked = false
                )
                val res = repo.getAvailableTimeSlots(req)
                res.onSuccess { slots ->
                    _timeSlots.value = slots
                }.onFailure { e ->
                    _error.value = e.message ?: "Error al cargar horarios"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error inesperado al cargar horarios"
            }
        }
    }

    fun reserveTimeSlot(serviceId: Int, userId: Int, timeSlotId: Int) {
        viewModelScope.launch {
            _reserving.value = true
            _error.value = null
            try {
                val req = ReservationRequest(service_id = serviceId, user_id = userId, time_slot_id = timeSlotId)
                val res = repo.createReservation(req)
                res.onSuccess { reservation ->
                    _reservationResult.value = reservation
                    // marcar que este servicio tiene reservado el timeSlot
                    _reservedMap.value = _reservedMap.value + (serviceId to reservation.time_slot_id)
                    // refrescar disponibilidad para este servicio (remover slot reservado)
                    val refreshReq = com.antaedo_karfuentealba.eva3_ambientefest.data.model.AvailableTimeSlotRequest(
                        service_id = serviceId,
                        date = "",
                        start_time = "",
                        end_time = "",
                        created_by_ = 0,
                        is_booked = false
                    )
                    val refreshed = repo.getAvailableTimeSlots(refreshReq)
                    refreshed.onSuccess { slots -> _timeSlots.value = slots }
                }.onFailure { e ->
                    _error.value = e.message ?: "Error al reservar horario"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error inesperado al reservar"
            } finally {
                _reserving.value = false
            }
        }
    }

    /** uploadImages y updateServiceImages existentes **/
    fun uploadImages(parts: List<okhttp3.MultipartBody.Part>, onComplete: (List<XanoImage>?) -> Unit = {}) {
        viewModelScope.launch {
            _uploading.value = true
            _uploadError.value = null
            _uploadResult.value = null
            try {
                val res = repo.uploadImages(parts)
                res.onSuccess { images ->
                    _uploadResult.value = images
                    onComplete(images)
                }.onFailure { e ->
                    _uploadError.value = e.message ?: "Error al subir imágenes"
                    onComplete(null)
                }
            } catch (e: Exception) {
                _uploadError.value = e.message ?: "Excepción al subir imágenes"
                onComplete(null)
            } finally {
                _uploading.value = false
            }
        }
    }

    fun updateServiceImages(serviceId: Int, imagenes: List<XanoImage>, onComplete: (Servicio?) -> Unit = {}) {
        viewModelScope.launch {
            _uploading.value = true
            _uploadError.value = null
            try {
                val res = repo.updateServiceImages(serviceId, imagenes)
                res.onSuccess { servicio ->
                    // actualizar la lista local si existe
                    _services.value = _services.value.map { if (it.id == servicio.id) servicio else it }
                    onComplete(servicio)
                }.onFailure { e ->
                    _uploadError.value = e.message ?: "Error al actualizar servicio"
                    onComplete(null)
                }
            } catch (e: Exception) {
                _uploadError.value = e.message ?: "Excepción al actualizar servicio"
                onComplete(null)
            } finally {
                _uploading.value = false
            }
        }
    }

    companion object {
        fun Factory(repo: ServiceRepository) = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ServiceViewModel(repo) as T
            }
        }
    }
}
