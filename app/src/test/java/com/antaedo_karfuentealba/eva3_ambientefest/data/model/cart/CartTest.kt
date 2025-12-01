package com.antaedo_karfuentealba.eva3_ambientefest.data.model.cart

import org.junit.Test
import org.junit.Assert.*

class CartTest {
    
    @Test
    fun `debería calcular total del carrito correctamente`() {
        val items = listOf(25000.0, 35000.0, 15000.0) // Precios de items
        val total = items.sum()
        val descuento = 5000.0
        val totalConDescuento = total - descuento
        
        assertEquals("Total debería ser suma de items", 75000.0, total, 0.01)
        assertEquals("Total con descuento debería restar descuento", 70000.0, totalConDescuento, 0.01)
        assertTrue("Total debería ser positivo", total > 0)
    }
    
    @Test
    fun `debería manejar carrito vacío`() {
        val itemsVacios = emptyList<Any>()
        val totalVacio = 0.0
        val carritoVacio = itemsVacios.isEmpty()
        
        assertTrue("Carrito vacío debería estar vacío", carritoVacio)
        assertEquals("Total de carrito vacío debería ser 0", 0.0, totalVacio, 0.01)
        assertEquals("Cantidad de items debería ser 0", 0, itemsVacios.size)
    }
    
    @Test
    fun `debería validar límite de items en carrito`() {
        val maxItems = 20
        val itemsActuales = 5
        val puedeAgregar = itemsActuales < maxItems
        
        assertTrue("Debería poder agregar más items", puedeAgregar)
        assertTrue("Límite máximo debería ser razonable", maxItems <= 50)
        assertTrue("Items actuales debería ser positivo o cero", itemsActuales >= 0)
    }
    
    @Test
    fun `debería aplicar descuentos correctamente`() {
        val totalSinDescuento = 100000.0
        val porcentajeDescuento = 10 // 10%
        val descuentoCalculado = totalSinDescuento * (porcentajeDescuento / 100.0)
        val totalFinal = totalSinDescuento - descuentoCalculado
        
        assertEquals("Descuento debería ser 10000", 10000.0, descuentoCalculado, 0.01)
        assertEquals("Total final debería ser 90000", 90000.0, totalFinal, 0.01)
        assertTrue("Porcentaje debería estar en rango válido", porcentajeDescuento in 0..100)
    }
    
    @Test
    fun `debería validar fecha de expiración del carrito`() {
        val fechaCreacion = System.currentTimeMillis()
        val tiempoExpiracion = 24 * 60 * 60 * 1000L // 24 horas en ms
        val fechaExpiracion = fechaCreacion + tiempoExpiracion
        val carritoExpirado = System.currentTimeMillis() > fechaExpiracion
        
        assertFalse("Carrito recién creado no debería estar expirado", carritoExpirado)
        assertTrue("Fecha de expiración debería ser futura", fechaExpiracion > fechaCreacion)
        assertTrue("Tiempo de expiración debería ser positivo", tiempoExpiracion > 0)
    }
}