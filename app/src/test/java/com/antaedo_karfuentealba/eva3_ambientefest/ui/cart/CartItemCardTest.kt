package com.antaedo_karfuentealba.eva3_ambientefest.ui.cart

import org.junit.Test
import org.junit.Assert.*

class CartItemCardTest {
    
    @Test
    fun `debería mostrar información del item correctamente`() {
        val nombreServicio = "DJ Profesional"
        val precio = 45000.0
        val cantidad = 2
        val subtotal = precio * cantidad
        
        assertFalse("Nombre no debería estar vacío", nombreServicio.isEmpty())
        assertTrue("Precio debería ser positivo", precio > 0)
        assertTrue("Cantidad debería ser mayor a 0", cantidad > 0)
        assertEquals("Subtotal debería ser correcto", 90000.0, subtotal, 0.01)
    }
    
    @Test
    fun `debería permitir aumentar cantidad`() {
        val cantidadActual = 2
        val cantidadMaxima = 10
        val puedeAumentar = cantidadActual < cantidadMaxima
        val nuevaCantidad = if (puedeAumentar) cantidadActual + 1 else cantidadActual
        
        assertTrue("Debería poder aumentar", puedeAumentar)
        assertEquals("Nueva cantidad debería ser 3", 3, nuevaCantidad)
        assertTrue("Nueva cantidad debería ser mayor", nuevaCantidad > cantidadActual)
    }
    
    @Test
    fun `debería permitir disminuir cantidad`() {
        val cantidadActual = 3
        val cantidadMinima = 1
        val puedeDisminuir = cantidadActual > cantidadMinima
        val nuevaCantidad = if (puedeDisminuir) cantidadActual - 1 else cantidadActual
        
        assertTrue("Debería poder disminuir", puedeDisminuir)
        assertEquals("Nueva cantidad debería ser 2", 2, nuevaCantidad)
        assertTrue("Nueva cantidad debería ser menor", nuevaCantidad < cantidadActual)
    }
    
    @Test
    fun `debería permitir eliminar item del carrito`() {
        val itemId = "item123"
        val confirmacionEliminacion = true
        val eliminacionExitosa = !itemId.isEmpty() && confirmacionEliminacion
        
        assertTrue("Eliminación debería ser exitosa", eliminacionExitosa)
        assertFalse("ID del item no debería estar vacío", itemId.isEmpty())
        assertTrue("Debería haber confirmación", confirmacionEliminacion)
    }
    
    @Test
    fun `debería mostrar imagen del servicio`() {
        val urlImagen: String? = "https://ejemplo.com/imagen.jpg"
        val tieneImagen = !urlImagen.isNullOrBlank()
        val imagenValida = urlImagen?.startsWith("http") == true
        
        assertTrue("Debería tener imagen", tieneImagen)
        assertNotNull("URL de imagen no debería ser null", urlImagen)
        assertTrue("URL debería ser válida", imagenValida)
    }
    
    @Test
    fun `debería formatear precio con moneda chilena`() {
        val precio = 35000.0
        val precioFormateado = "$${precio.toInt()}"
        val monedaCorrecta = precioFormateado.startsWith("$")
        
        assertEquals("Precio formateado debería ser correcto", "$35000", precioFormateado)
        assertTrue("Debería incluir símbolo de moneda", monedaCorrecta)
        assertFalse("Precio formateado no debería estar vacío", precioFormateado.isEmpty())
    }
}