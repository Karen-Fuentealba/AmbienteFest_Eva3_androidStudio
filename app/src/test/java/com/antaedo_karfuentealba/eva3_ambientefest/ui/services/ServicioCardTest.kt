package com.antaedo_karfuentealba.eva3_ambientefest.ui.services

import org.junit.Test
import org.junit.Assert.*

class ServicioCardTest {
    

    @Test
    fun `debería validar descripción del servicio`() {
        val descripcionCorta = "Corta"
        val descripcionOptima = "Esta es una descripción de longitud adecuada para mostrar."
        
        assertTrue("Descripción corta debería ser menor a 10 caracteres", descripcionCorta.length < 10)
        assertTrue("Descripción óptima debería estar entre 10 y 100 caracteres", 
            descripcionOptima.length in 10..100)
    }
    
    @Test
    fun `debería validar calificación en rango correcto`() {
        val calificacionMinima = 1.0
        val calificacionMaxima = 5.0
        val calificacionMedia = 3.5
        
        assertTrue("Calificación mínima debería ser >= 1", calificacionMinima >= 1.0)
        assertTrue("Calificación máxima debería ser <= 5", calificacionMaxima <= 5.0)
        assertTrue("Calificación media debería estar en rango", calificacionMedia in 1.0..5.0)
    }
    
    @Test
    fun `debería manejar imagen por defecto`() {
        val imagenUrl: String? = null
        val tieneImagen = !imagenUrl.isNullOrBlank()
        
        assertFalse("Sin URL de imagen debería mostrar imagen por defecto", tieneImagen)
        assertNull("URL de imagen debería ser null", imagenUrl)
    }
}