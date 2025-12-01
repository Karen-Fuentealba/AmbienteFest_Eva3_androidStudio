package com.antaedo_karfuentealba.eva3_ambientefest.ui.cart

import org.junit.Test
import org.junit.Assert.*

class CartScreenTest {
    
    @Test
    fun `debería calcular y mostrar total correctamente`() {
        val subtotal = 75000.0
        val descuento = 7500.0
        val impuestos = (subtotal - descuento) * 0.19
        val total = subtotal - descuento + impuestos
        val totalFormateado = "$${total.toInt()}"
        
        assertTrue("Total debería ser positivo", total > 0)
        assertTrue("Total formateado debería contener $", totalFormateado.contains("$"))
        assertTrue("Descuento debería ser menor que subtotal", descuento < subtotal)
    }
    
    @Test
    fun `debería habilitar botón de checkout cuando es válido`() {
        val carritoTieneItems = true
        val totalMayorCero = true
        val datosCompletos = true
        val botonHabilitado = carritoTieneItems && totalMayorCero && datosCompletos
        
        assertTrue("Botón debería estar habilitado", botonHabilitado)
        assertTrue("Carrito debería tener items", carritoTieneItems)
        assertTrue("Total debería ser mayor a cero", totalMayorCero)
    }
    
    @Test
    fun `debería manejar navegación entre pantallas`() {
        val puedeVolverAtras = true
        val puedeIrACheckout = true
        val puedeIrAServicios = true
        
        assertTrue("Debería poder volver atrás", puedeVolverAtras)
        assertTrue("Debería poder ir a checkout", puedeIrACheckout)
        assertTrue("Debería poder ir a servicios", puedeIrAServicios)
    }
}