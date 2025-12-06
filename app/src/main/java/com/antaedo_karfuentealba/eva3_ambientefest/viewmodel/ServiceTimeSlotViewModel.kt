package com.antaedo_karfuentealba.eva3_ambientefest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.TimeSlot
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ServiceTimeSlotUiState(
    val timeSlotsByService: Map<Int, List<TimeSlot>> = emptyMap(),
    val loadingByService: Map<Int, Boolean> = emptyMap(),
    val errorByService: Map<Int, String?> = emptyMap(),
    val showTimeSlotsDialog: Boolean = false,
    val showCreateTimeSlotDialog: Boolean = false,
    val showEditTimeSlotDialog: Boolean = false,
    val showDeleteTimeSlotDialog: Boolean = false,
    val selectedServiceId: Int? = null,
    val selectedTimeSlot: TimeSlot? = null,
    val createDate: String = "",
    val createStartTime: String = "",
    val createEndTime: String = "",
    val editDate: String = "",
    val editStartTime: String = "",
    val editEndTime: String = "",
    val successMessage: String? = null,
    val globalLoading: Boolean = false
)

class ServiceTimeSlotViewModel(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceTimeSlotUiState())
    val uiState: StateFlow<ServiceTimeSlotUiState> = _uiState

    // Listar horarios de un servicio
    fun loadTimeSlots(serviceId: Int) {
        _uiState.value = _uiState.value.copy(
            loadingByService = _uiState.value.loadingByService + mapOf(serviceId to true),
            errorByService = _uiState.value.errorByService + mapOf(serviceId to null)
        )

        viewModelScope.launch {
            try {
                val result = serviceRepository.getTimeSlotsByService(serviceId)
                if (result.isSuccess) {
                    val timeSlots = result.getOrNull() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        timeSlotsByService = _uiState.value.timeSlotsByService + mapOf(serviceId to timeSlots),
                        loadingByService = _uiState.value.loadingByService + mapOf(serviceId to false)
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        loadingByService = _uiState.value.loadingByService + mapOf(serviceId to false),
                        errorByService = _uiState.value.errorByService + mapOf(serviceId to (result.exceptionOrNull()?.message ?: "Error al cargar horarios"))
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loadingByService = _uiState.value.loadingByService + mapOf(serviceId to false),
                    errorByService = _uiState.value.errorByService + mapOf(serviceId to (e.message ?: "Error inesperado"))
                )
            }
        }
    }

    // Mostrar modal de listar horarios
    fun showTimeSlotsDialog(serviceId: Int) {
        _uiState.value = _uiState.value.copy(
            showTimeSlotsDialog = true,
            selectedServiceId = serviceId
        )
        loadTimeSlots(serviceId)
    }

    fun hideTimeSlotsDialog() {
        _uiState.value = _uiState.value.copy(
            showTimeSlotsDialog = false,
            selectedServiceId = null
        )
    }

    // Crear horario - mostrar modal
    fun showCreateTimeSlotDialog(serviceId: Int) {
        _uiState.value = _uiState.value.copy(
            showCreateTimeSlotDialog = true,
            selectedServiceId = serviceId,
            createDate = "",
            createStartTime = "",
            createEndTime = ""
        )
    }

    fun hideCreateTimeSlotDialog() {
        _uiState.value = _uiState.value.copy(
            showCreateTimeSlotDialog = false,
            selectedServiceId = null,
            createDate = "",
            createStartTime = "",
            createEndTime = ""
        )
    }

    fun onCreateTimeSlotFieldChanged(
        date: String? = null,
        startTime: String? = null,
        endTime: String? = null
    ) {
        _uiState.value = _uiState.value.copy(
            createDate = date ?: _uiState.value.createDate,
            createStartTime = startTime ?: _uiState.value.createStartTime,
            createEndTime = endTime ?: _uiState.value.createEndTime
        )
    }

    fun createTimeSlot() {
        val serviceId = _uiState.value.selectedServiceId ?: return
        val state = _uiState.value

        // Validaciones
        if (state.createDate.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorByService = _uiState.value.errorByService + mapOf(serviceId to "La fecha es requerida")
            )
            return
        }

        if (state.createStartTime.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorByService = _uiState.value.errorByService + mapOf(serviceId to "La hora de inicio es requerida")
            )
            return
        }

        if (state.createEndTime.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorByService = _uiState.value.errorByService + mapOf(serviceId to "La hora de fin es requerida")
            )
            return
        }

        // Validar que hora inicio < hora fin
        if (!isValidTimeRange(state.createStartTime, state.createEndTime)) {
            _uiState.value = _uiState.value.copy(
                errorByService = _uiState.value.errorByService + mapOf(serviceId to "La hora de inicio debe ser menor que la hora de fin")
            )
            return
        }

        // Validar solapamientos
        if (hasTimeOverlap(serviceId, state.createDate, state.createStartTime, state.createEndTime)) {
            _uiState.value = _uiState.value.copy(
                errorByService = _uiState.value.errorByService + mapOf(serviceId to "Ya existe un horario que se solapa en esa fecha y hora")
            )
            return
        }

        _uiState.value = _uiState.value.copy(globalLoading = true)

        viewModelScope.launch {
            try {
                val request = CreateTimeSlotRequest(
                    service_id = serviceId,
                    date = state.createDate,
                    start_time = state.createStartTime,
                    end_time = state.createEndTime,
                    is_booked = false
                )

                val result = serviceRepository.createTimeSlot(request)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        globalLoading = false,
                        showCreateTimeSlotDialog = false,
                        successMessage = "Horario creado exitosamente",
                        createDate = "",
                        createStartTime = "",
                        createEndTime = ""
                    )
                    // Recargar horarios para este servicio
                    loadTimeSlots(serviceId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        globalLoading = false,
                        errorByService = _uiState.value.errorByService + mapOf(serviceId to (result.exceptionOrNull()?.message ?: "Error al crear horario"))
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    globalLoading = false,
                    errorByService = _uiState.value.errorByService + mapOf(serviceId to (e.message ?: "Error inesperado"))
                )
            }
        }
    }

    // Editar horario - mostrar modal
    fun showEditTimeSlotDialog(timeSlot: TimeSlot) {
        _uiState.value = _uiState.value.copy(
            showEditTimeSlotDialog = true,
            selectedTimeSlot = timeSlot,
            selectedServiceId = timeSlot.service_id,
            editDate = timeSlot.date,
            editStartTime = timeSlot.start_time,
            editEndTime = timeSlot.end_time
        )
    }

    fun hideEditTimeSlotDialog() {
        _uiState.value = _uiState.value.copy(
            showEditTimeSlotDialog = false,
            selectedTimeSlot = null,
            editDate = "",
            editStartTime = "",
            editEndTime = ""
        )
    }

    fun onEditTimeSlotFieldChanged(
        date: String? = null,
        startTime: String? = null,
        endTime: String? = null
    ) {
        _uiState.value = _uiState.value.copy(
            editDate = date ?: _uiState.value.editDate,
            editStartTime = startTime ?: _uiState.value.editStartTime,
            editEndTime = endTime ?: _uiState.value.editEndTime
        )
    }

    fun updateTimeSlot() {
        val timeSlot = _uiState.value.selectedTimeSlot ?: return
        val serviceId = timeSlot.service_id
        val state = _uiState.value

        // Validaciones
        if (state.editDate.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorByService = _uiState.value.errorByService + mapOf(serviceId to "La fecha es requerida")
            )
            return
        }

        if (state.editStartTime.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorByService = _uiState.value.errorByService + mapOf(serviceId to "La hora de inicio es requerida")
            )
            return
        }

        if (state.editEndTime.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorByService = _uiState.value.errorByService + mapOf(serviceId to "La hora de fin es requerida")
            )
            return
        }

        // Validar que hora inicio < hora fin
        if (!isValidTimeRange(state.editStartTime, state.editEndTime)) {
            _uiState.value = _uiState.value.copy(
                errorByService = _uiState.value.errorByService + mapOf(serviceId to "La hora de inicio debe ser menor que la hora de fin")
            )
            return
        }

        // Validar solapamientos (excluyendo el horario actual)
        if (hasTimeOverlap(serviceId, state.editDate, state.editStartTime, state.editEndTime, timeSlot.id)) {
            _uiState.value = _uiState.value.copy(
                errorByService = _uiState.value.errorByService + mapOf(serviceId to "Ya existe un horario que se solapa en esa fecha y hora")
            )
            return
        }

        _uiState.value = _uiState.value.copy(globalLoading = true)

        viewModelScope.launch {
            try {
                val request = UpdateTimeSlotRequest(
                    date = state.editDate,
                    start_time = state.editStartTime,
                    end_time = state.editEndTime
                )

                val result = serviceRepository.updateTimeSlot(timeSlot.id, request)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        globalLoading = false,
                        showEditTimeSlotDialog = false,
                        successMessage = "Horario actualizado exitosamente"
                    )
                    // Recargar horarios para este servicio
                    loadTimeSlots(serviceId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        globalLoading = false,
                        errorByService = _uiState.value.errorByService + mapOf(serviceId to (result.exceptionOrNull()?.message ?: "Error al actualizar horario"))
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    globalLoading = false,
                    errorByService = _uiState.value.errorByService + mapOf(serviceId to (e.message ?: "Error inesperado"))
                )
            }
        }
    }

    // Eliminar horario
    fun showDeleteTimeSlotDialog(timeSlot: TimeSlot) {
        _uiState.value = _uiState.value.copy(
            showDeleteTimeSlotDialog = true,
            selectedTimeSlot = timeSlot,
            selectedServiceId = timeSlot.service_id
        )
    }

    fun hideDeleteTimeSlotDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteTimeSlotDialog = false,
            selectedTimeSlot = null
        )
    }

    fun deleteTimeSlot() {
        val timeSlot = _uiState.value.selectedTimeSlot ?: return
        val serviceId = timeSlot.service_id

        _uiState.value = _uiState.value.copy(globalLoading = true)

        viewModelScope.launch {
            try {
                val result = serviceRepository.deleteTimeSlot(timeSlot.id)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        globalLoading = false,
                        showDeleteTimeSlotDialog = false,
                        successMessage = "Horario eliminado exitosamente"
                    )
                    // Recargar horarios para este servicio
                    loadTimeSlots(serviceId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        globalLoading = false,
                        errorByService = _uiState.value.errorByService + mapOf(serviceId to (result.exceptionOrNull()?.message ?: "Error al eliminar horario"))
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    globalLoading = false,
                    errorByService = _uiState.value.errorByService + mapOf(serviceId to (e.message ?: "Error inesperado"))
                )
            }
        }
    }

    // Limpiar mensajes
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun clearErrorMessage(serviceId: Int) {
        _uiState.value = _uiState.value.copy(
            errorByService = _uiState.value.errorByService + mapOf(serviceId to null)
        )
    }

    // Funciones de validación
    private fun isValidTimeRange(startTime: String, endTime: String): Boolean {
        return try {
            val startMinutes = timeToMinutes(startTime)
            val endMinutes = timeToMinutes(endTime)
            startMinutes >= 0 && endMinutes >= 0 && startMinutes < endMinutes
        } catch (e: Exception) {
            false
        }
    }

    private fun hasTimeOverlap(
        serviceId: Int,
        date: String,
        startTime: String,
        endTime: String,
        excludeTimeSlotId: Int? = null
    ): Boolean {
        val existingTimeSlots = _uiState.value.timeSlotsByService[serviceId] ?: return false

        return existingTimeSlots
            .filter { it.id != excludeTimeSlotId } // Excluir el horario actual en caso de edición
            .filter { it.date == date } // Mismo día
            .any { existing ->
                try {
                    // Convertir horarios a minutos para comparación más simple
                    val newStartMinutes = timeToMinutes(startTime)
                    val newEndMinutes = timeToMinutes(endTime)
                    val existingStartMinutes = timeToMinutes(existing.start_time)
                    val existingEndMinutes = timeToMinutes(existing.end_time)

                    // Verificar solapamiento: nuevo inicio < existente fin && nuevo fin > existente inicio
                    newStartMinutes < existingEndMinutes && newEndMinutes > existingStartMinutes
                } catch (e: Exception) {
                    false
                }
            }
    }

    // Función auxiliar para convertir tiempo HH:mm a minutos
    private fun timeToMinutes(time: String): Int {
        return try {
            val parts = time.split(":")
            if (parts.size != 2) return -1
            val hours = parts[0].toIntOrNull() ?: return -1
            val minutes = parts[1].toIntOrNull() ?: return -1
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) return -1
            hours * 60 + minutes
        } catch (e: Exception) {
            -1
        }
    }

    companion object {
        fun Factory(serviceRepository: ServiceRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ServiceTimeSlotViewModel(serviceRepository) as T
            }
        }
    }
}

// Data classes para las requests
data class CreateTimeSlotRequest(
    val service_id: Int,
    val date: String,
    val start_time: String,
    val end_time: String,
    val is_booked: Boolean = false
)

data class UpdateTimeSlotRequest(
    val date: String? = null,
    val start_time: String? = null,
    val end_time: String? = null,
    val is_booked: Boolean? = null
)
