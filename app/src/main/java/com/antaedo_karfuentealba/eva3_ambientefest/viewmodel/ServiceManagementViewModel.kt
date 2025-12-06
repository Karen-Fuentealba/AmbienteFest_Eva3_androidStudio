package com.antaedo_karfuentealba.eva3_ambientefest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.XanoImage
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.CreateServiceRequest
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

data class ServiceManagementUiState(
    val services: List<Servicio> = emptyList(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showCreateDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val selectedService: Servicio? = null,
    // Create service fields
    val createServiceName: String = "",
    val createServiceDescription: String = "",
    val createServicePrice: String = "",
    val createServiceProvider: String = "",
    val createServiceAvailability: String = "Disponible",
    val createServiceStatus: String = "active",
    val createServiceImages: List<XanoImage> = emptyList(),
    // Edit service fields
    val editServiceName: String = "",
    val editServiceDescription: String = "",
    val editServicePrice: String = "",
    val editServiceProvider: String = "",
    val editServiceAvailability: String = "",
    val editServiceStatus: String = "",
    val editServiceImages: List<XanoImage> = emptyList(),
    // Image upload state
    val uploadingImages: Boolean = false
)

class ServiceManagementViewModel(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceManagementUiState())
    val uiState: StateFlow<ServiceManagementUiState> = _uiState

    init {
        loadServices()
    }

    private fun loadServices() {
        _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)

        viewModelScope.launch {
            val result = serviceRepository.getServices()

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    services = result.getOrNull() ?: emptyList(),
                    loading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar servicios"
                )
            }
        }
    }

    fun refreshServices() {
        loadServices()
    }

    // Create service methods
    fun showCreateServiceDialog() {
        _uiState.value = _uiState.value.copy(
            showCreateDialog = true,
            createServiceName = "",
            createServiceDescription = "",
            createServicePrice = "",
            createServiceProvider = "",
            createServiceAvailability = "Disponible",
            createServiceStatus = "active",
            createServiceImages = emptyList(),
            errorMessage = null,
            successMessage = null
        )
    }

    fun hideCreateServiceDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun onCreateServiceFieldChanged(
        name: String? = null,
        description: String? = null,
        price: String? = null,
        provider: String? = null,
        availability: String? = null,
        status: String? = null,
        images: List<XanoImage>? = null
    ) {
        _uiState.value = _uiState.value.copy(
            createServiceName = name ?: _uiState.value.createServiceName,
            createServiceDescription = description ?: _uiState.value.createServiceDescription,
            createServicePrice = price ?: _uiState.value.createServicePrice,
            createServiceProvider = provider ?: _uiState.value.createServiceProvider,
            createServiceAvailability = availability ?: _uiState.value.createServiceAvailability,
            createServiceStatus = status ?: _uiState.value.createServiceStatus,
            createServiceImages = images ?: _uiState.value.createServiceImages,
            errorMessage = null
        )
    }

    fun createService() {
        val state = _uiState.value

        // Validations
        if (state.createServiceName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El nombre es requerido")
            return
        }
        if (state.createServiceDescription.isBlank()) {
            _uiState.value = state.copy(errorMessage = "La descripción es requerida")
            return
        }
        if (state.createServicePrice.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El precio es requerido")
            return
        }
        if (state.createServiceProvider.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El proveedor es requerido")
            return
        }

        val price = state.createServicePrice.toDoubleOrNull()
        if (price == null || price <= 0) {
            _uiState.value = state.copy(errorMessage = "Ingrese un precio válido")
            return
        }

        _uiState.value = state.copy(loading = true, errorMessage = null)

        viewModelScope.launch {
            val request = CreateServiceRequest(
                name = state.createServiceName,
                description = state.createServiceDescription,
                price = price,
                provider = state.createServiceProvider,
                availability = state.createServiceAvailability,
                status = state.createServiceStatus,
                imagen = state.createServiceImages
            )

            val result = serviceRepository.createService(request)

            if (result.isSuccess) {
                val newService = result.getOrNull()
                if (newService != null) {
                    // Update local list immediately
                    val updatedServices = _uiState.value.services + newService
                    _uiState.value = _uiState.value.copy(
                        services = updatedServices,
                        loading = false,
                        showCreateDialog = false,
                        successMessage = "Servicio creado exitosamente"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        showCreateDialog = false,
                        successMessage = "Servicio creado exitosamente"
                    )
                    loadServices() // Fallback to reload
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al crear servicio"
                )
            }
        }
    }

    // Edit service methods
    fun showEditServiceDialog(service: Servicio) {
        _uiState.value = _uiState.value.copy(
            showEditDialog = true,
            selectedService = service,
            editServiceName = service.name,
            editServiceDescription = service.description,
            editServicePrice = service.price.toString(),
            editServiceProvider = service.provider,
            editServiceAvailability = service.availability ?: "Disponible",
            editServiceStatus = service.status ?: "active",
            editServiceImages = service.imagen,
            errorMessage = null,
            successMessage = null
        )
    }

    fun hideEditServiceDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = false, selectedService = null)
    }

    fun onEditServiceFieldChanged(
        name: String? = null,
        description: String? = null,
        price: String? = null,
        provider: String? = null,
        availability: String? = null,
        status: String? = null,
        images: List<XanoImage>? = null
    ) {
        _uiState.value = _uiState.value.copy(
            editServiceName = name ?: _uiState.value.editServiceName,
            editServiceDescription = description ?: _uiState.value.editServiceDescription,
            editServicePrice = price ?: _uiState.value.editServicePrice,
            editServiceProvider = provider ?: _uiState.value.editServiceProvider,
            editServiceAvailability = availability ?: _uiState.value.editServiceAvailability,
            editServiceStatus = status ?: _uiState.value.editServiceStatus,
            editServiceImages = images ?: _uiState.value.editServiceImages,
            errorMessage = null
        )
    }

    fun updateService() {
        val state = _uiState.value
        val service = state.selectedService ?: return

        // Validations
        if (state.editServiceName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El nombre es requerido")
            return
        }
        if (state.editServiceDescription.isBlank()) {
            _uiState.value = state.copy(errorMessage = "La descripción es requerida")
            return
        }
        if (state.editServicePrice.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El precio es requerido")
            return
        }
        if (state.editServiceProvider.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El proveedor es requerido")
            return
        }

        val price = state.editServicePrice.toDoubleOrNull()
        if (price == null || price <= 0) {
            _uiState.value = state.copy(errorMessage = "Ingrese un precio válido")
            return
        }

        _uiState.value = state.copy(loading = true, errorMessage = null)

        viewModelScope.launch {
            val request = com.antaedo_karfuentealba.eva3_ambientefest.data.repository.UpdateServiceRequest(
                name = state.editServiceName,
                description = state.editServiceDescription,
                price = price,
                provider = state.editServiceProvider,
                availability = state.editServiceAvailability,
                status = state.editServiceStatus,
                imagen = state.editServiceImages
            )

            val result = serviceRepository.updateService(service.id, request)

            if (result.isSuccess) {
                val updatedService = result.getOrNull()
                if (updatedService != null) {
                    // Update local list immediately
                    val updatedServices = _uiState.value.services.map { existingService ->
                        if (existingService.id == updatedService.id) updatedService else existingService
                    }
                    _uiState.value = _uiState.value.copy(
                        services = updatedServices,
                        loading = false,
                        showEditDialog = false,
                        successMessage = "Servicio actualizado exitosamente"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        showEditDialog = false,
                        successMessage = "Servicio actualizado exitosamente"
                    )
                    loadServices() // Fallback to reload
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al actualizar servicio"
                )
            }
        }
    }

    // Delete service methods
    fun showDeleteServiceDialog(service: Servicio) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            selectedService = service,
            errorMessage = null
        )
    }

    fun hideDeleteServiceDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false, selectedService = null)
    }

    fun deleteService() {
        val service = _uiState.value.selectedService ?: return

        _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)

        viewModelScope.launch {
            val result = serviceRepository.deleteService(service.id)

            if (result.isSuccess) {
                // Update local list immediately
                val updatedServices = _uiState.value.services.filter { it.id != service.id }
                _uiState.value = _uiState.value.copy(
                    services = updatedServices,
                    loading = false,
                    showDeleteDialog = false,
                    successMessage = "Servicio eliminado exitosamente"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al eliminar servicio"
                )
            }
        }
    }

    // Image upload methods
    fun uploadImages(parts: List<MultipartBody.Part>, isForEdit: Boolean = false) {
        _uiState.value = _uiState.value.copy(uploadingImages = true, errorMessage = null)

        viewModelScope.launch {
            val result = serviceRepository.uploadImages(parts)

            if (result.isSuccess) {
                val images = result.getOrNull() ?: emptyList()
                if (isForEdit) {
                    _uiState.value = _uiState.value.copy(
                        editServiceImages = _uiState.value.editServiceImages + images,
                        uploadingImages = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        createServiceImages = _uiState.value.createServiceImages + images,
                        uploadingImages = false
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    uploadingImages = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error al subir imágenes"
                )
            }
        }
    }

    fun removeImage(imageToRemove: XanoImage, isForEdit: Boolean = false) {
        if (isForEdit) {
            val updatedImages = _uiState.value.editServiceImages.filter { it.path != imageToRemove.path }
            _uiState.value = _uiState.value.copy(editServiceImages = updatedImages)
        } else {
            val updatedImages = _uiState.value.createServiceImages.filter { it.path != imageToRemove.path }
            _uiState.value = _uiState.value.copy(createServiceImages = updatedImages)
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    companion object {
        fun Factory(serviceRepository: ServiceRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ServiceManagementViewModel(serviceRepository) as T
            }
        }
    }
}
