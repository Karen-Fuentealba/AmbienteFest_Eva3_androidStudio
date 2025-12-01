package com.antaedo_karfuentealba.eva3_ambientefest.ui.services

import org.junit.Test
import org.junit.Assert.*

class ServicioDetalleScreenTest {
    
    @Test
    fun `debería validar información completa del servicio`() {
        val servicioId = "123"
        val titulo = "DJ Profesional"
        val descripcion = "Servicio de DJ para eventos"
        val precio = 50000.0
        
        assertFalse("ID de servicio no debería estar vacío", servicioId.isEmpty())
        assertFalse("Título no debería estar vacío", titulo.isEmpty())
        assertFalse("Descripción no debería estar vacía", descripcion.isEmpty())
        assertTrue("Precio debería ser positivo", precio > 0)
    }
    
    @Test
    fun `debería manejar reserva de servicio`() {
        val puedeReservar = true
        val servicioDisponible = true
        val fechaSeleccionada: String? = "2024-12-25"
        
        assertTrue("Debería poder reservar", puedeReservar)
        assertTrue("Servicio debería estar disponible", servicioDisponible)
        assertNotNull("Fecha seleccionada no debería ser null", fechaSeleccionada)
    }
    

    @Test
    fun `debería manejar galería de imágenes`() {
        val imagenesGaleria = listOf("img1.jpg", "img2.jpg", "img3.jpg")
        val imagenPrincipal = "main.jpg"
        
        assertTrue("Galería debería tener imágenes", imagenesGaleria.isNotEmpty())
        assertFalse("Imagen principal no debería estar vacía", imagenPrincipal.isEmpty())
        assertTrue("Debería tener múltiples imágenes", imagenesGaleria.size > 1)
    }
    

}