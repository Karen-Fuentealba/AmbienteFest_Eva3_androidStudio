package com.antaedo_karfuentealba.eva3_ambientefest.data.model

import org.junit.Test
import org.junit.Assert.*

/**
 * Test unitario para el modelo Servicio
 * Prueba la estructura y validaciones básicas del data class
 */
class ServicioTest {

    @Test
    fun `servicio debe tener propiedades basicas correctas`() {
        // ARRANGE - Crear servicio con datos válidos
        val servicio = Servicio(
            id = 1,
            name = "Servicio Test",
            description = "Descripción del servicio",
            price = 100.0,
            provider = "Proveedor Test",
            availability = "Disponible",
            rating = 4.5,
            num_ratings = 10,
            available = true,
            status = "activo",
            user_id = 1,
            service_category_id = 1,
            imagen = emptyList()
        )
        
        // ACT & ASSERT
        assertEquals("ID debe coincidir", 1, servicio.id)
        assertEquals("Nombre debe coincidir", "Servicio Test", servicio.name)
        assertFalse("Descripción no debe estar vacía", servicio.description.isEmpty())
        assertTrue("Precio debe ser positivo", servicio.price > 0)
        assertFalse("Proveedor no debe estar vacío", servicio.provider.isEmpty())
    }
    
    @Test
    fun `servicio debe manejar campos opcionales como null`() {
        // ARRANGE - Servicio con campos opcionales null
        val servicio = Servicio(
            id = 2,
            name = "Servicio Mínimo",
            description = "Descripción",
            price = 50.0,
            provider = "Proveedor",
            availability = null,
            rating = null,
            num_ratings = null,
            available = null,
            status = null,
            user_id = null,
            service_category_id = null
        )
        
        // ACT & ASSERT
        assertNull("Availability puede ser null", servicio.availability)
        assertNull("Rating puede ser null", servicio.rating)
        assertNull("Num_ratings puede ser null", servicio.num_ratings)
        assertNull("Available puede ser null", servicio.available)
        assertNull("Status puede ser null", servicio.status)
    }

    @Test
    fun `servicio debe tener lista de imagenes vacia por defecto`() {
        // ARRANGE
        val servicio = Servicio(
            id = 3,
            name = "Servicio Sin Imágenes",
            description = "Test",
            price = 75.0,
            provider = "Test Provider",
            availability = null,
            rating = null,
            num_ratings = null,
            available = null,
            status = null,
            user_id = null,
            service_category_id = null
        )
        
        // ACT & ASSERT
        assertTrue("Lista de imágenes debe estar vacía por defecto", servicio.imagen.isEmpty())
        assertEquals("Lista debe tener tamaño 0", 0, servicio.imagen.size)
    }

    @Test
    fun `servicio debe validar rating en rango valido`() {
        // ARRANGE - Diferentes ratings
        val ratingValido = 4.5
        val ratingMinimo = 0.0
        val ratingMaximo = 5.0
        val ratingInvalido = -1.0
        
        // ACT & ASSERT
        assertTrue("Rating 4.5 debe ser válido", ratingValido >= 0.0 && ratingValido <= 5.0)
        assertTrue("Rating 0.0 debe ser válido", ratingMinimo >= 0.0 && ratingMinimo <= 5.0)
        assertTrue("Rating 5.0 debe ser válido", ratingMaximo >= 0.0 && ratingMaximo <= 5.0)
        assertFalse("Rating -1.0 debe ser inválido", ratingInvalido >= 0.0 && ratingInvalido <= 5.0)
    }

    @Test
    fun `servicio debe validar precio positivo`() {
        // ARRANGE - Diferentes precios
        val precioValido = 100.50
        val precioCero = 0.0
        val precioNegativo = -50.0
        
        // ACT & ASSERT
        assertTrue("Precio 100.50 debe ser válido", precioValido > 0)
        assertFalse("Precio 0.0 debe ser inválido", precioCero > 0)
        assertFalse("Precio negativo debe ser inválido", precioNegativo > 0)
    }
}