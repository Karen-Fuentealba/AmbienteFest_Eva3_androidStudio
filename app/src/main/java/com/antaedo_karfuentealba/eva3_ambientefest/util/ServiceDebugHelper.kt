package com.antaedo_karfuentealba.eva3_ambientefest.util

import android.util.Log
import com.antaedo_karfuentealba.eva3_ambientefest.data.model.Servicio
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.ServiceRepository

object ServiceDebugHelper {
    private const val TAG = "ServiceDebugHelper"

    suspend fun testServiceLoading() {
        try {
            Log.d(TAG, "Iniciando test de carga de servicios...")
            val repository = ServiceRepository()
            val result = repository.getServices()

            result.onSuccess { servicios ->
                Log.d(TAG, "Servicios cargados exitosamente: ${servicios.size}")
                servicios.forEachIndexed { index, servicio ->
                    Log.d(TAG, "Servicio $index: ${servicio.name}")
                    Log.d(TAG, "  - ID: ${servicio.id}")
                    Log.d(TAG, "  - Precio: ${servicio.price}")
                    Log.d(TAG, "  - Proveedor: ${servicio.provider}")
                    Log.d(TAG, "  - Imágenes: ${servicio.imagen.size}")
                    servicio.imagen.forEachIndexed { imgIndex, imagen ->
                        Log.d(TAG, "    Imagen $imgIndex: ${imagen.path}")
                    }
                }
            }.onFailure { error ->
                Log.e(TAG, "Error al cargar servicios: ${error.message}", error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al cargar servicios: ${e.message}", e)
        }
    }

    fun logServicioDetails(servicio: Servicio) {
        Log.d(TAG, "Detalles del servicio:")
        Log.d(TAG, "  - Nombre: ${servicio.name}")
        Log.d(TAG, "  - Descripción: ${servicio.description}")
        Log.d(TAG, "  - Precio: ${servicio.price}")
        Log.d(TAG, "  - Rating: ${servicio.rating}")
        Log.d(TAG, "  - Disponibilidad: ${servicio.availability}")
        Log.d(TAG, "  - Total imágenes: ${servicio.imagen.size}")
    }
}
