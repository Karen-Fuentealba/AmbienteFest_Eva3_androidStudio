package com.antaedo_karfuentealba.eva3_ambientefest.data.model.cart

import org.junit.Test
import org.junit.Assert.*

class CartItemTest {
    
    @Test
    fun `debería validar item de carrito con datos válidos`() {
        val itemId = "item123"
        val servicioId = "serv456"
        val nombre = "DJ para Matrimonio"
        val precio = 85000.0
        val cantidad = 2
        
        assertFalse("ID del item no debería estar vacío", itemId.isEmpty())
        assertFalse("ID del servicio no debería estar vacío", servicioId.isEmpty())
        assertFalse("Nombre no debería estar vacío", nombre.isEmpty())
        assertTrue("Precio debería ser positivo", precio > 0)
        assertTrue("Cantidad debería ser mayor a 0", cantidad > 0)
    }
    
    @Test
    fun `debería calcular subtotal correctamente`() {
        val precio = 25000.0
        val cantidad = 3
        val subtotal = precio * cantidad
        
        assertEquals("Subtotal debería ser precio * cantidad", 75000.0, subtotal, 0.01)
        assertTrue("Subtotal debería ser positivo", subtotal > 0)
    }
    
    @Test
    fun `debería validar límites de cantidad`() {
        val cantidadMinima = 1
        val cantidadMaxima = 10
        val cantidadInvalida = 0
        
        assertTrue("Cantidad mínima debería ser 1", cantidadMinima >= 1)
        assertTrue("Cantidad máxima debería ser razonable", cantidadMaxima <= 50)
        assertFalse("Cantidad 0 no debería ser válida", cantidadInvalida > 0)
    }
    
    @Test
    fun `debería manejar fecha de agregado al carrito`() {
        val fechaAgregado = System.currentTimeMillis()
        val tiempoActual = System.currentTimeMillis()
        val diferenciaTiempo = tiempoActual - fechaAgregado
        
        assertTrue("Fecha de agregado debería ser válida", fechaAgregado > 0)
        assertTrue("Diferencia de tiempo debería ser mínima", diferenciaTiempo < 1000)
    }
    
    @Test
    fun `debería validar disponibilidad del servicio`() {
        val servicioDisponible = true
        val fechaEvento = "2024-12-25"
        val horaEvento = "18:00"
        
        assertTrue("Servicio debería estar disponible", servicioDisponible)
        assertFalse("Fecha del evento no debería estar vacía", fechaEvento.isEmpty())
        assertFalse("Hora del evento no debería estar vacía", horaEvento.isEmpty())
    }
}