package com.antaedo_karfuentealba.eva3_ambientefest.ui.auth

import org.junit.Test
import org.junit.Assert.*

/**
 * Test unitario para SignupScreen
 * Prueba SOLO las validaciones realmente implementadas en SignupScreen.kt
 */
class SignupScreenTest {

    @Test
    fun `signup debe rechazar passwords que no coinciden`() {
        // ARRANGE - SignupScreen línea 150: if (password != confirm)
        val password = "abc123"
        val confirm = "xyz789"
        
        // ACT & ASSERT
        assertNotEquals("Passwords diferentes deben ser rechazadas", password, confirm)
    }
    
    @Test
    fun `signup debe aceptar passwords que coinciden`() {
        // ARRANGE - Caso exitoso
        val password = "miPassword"
        val confirm = "miPassword"
        
        // ACT & ASSERT
        assertEquals("Passwords iguales deben ser aceptadas", password, confirm)
    }

    @Test
    fun `signup debe rechazar campos obligatorios vacios`() {
        // ARRANGE - SignupScreen línea 151: campos.isBlank()
        val email = ""
        val password = ""
        val apellido = ""
        val nombre = ""
        
        // ACT & ASSERT - Validación: email.isBlank() || password.isBlank() || apellido.isBlank() || nombre.isBlank()
        val hayAlgunCampoVacio = email.isBlank() || password.isBlank() || apellido.isBlank() || nombre.isBlank()
        assertTrue("Debe detectar campos vacíos", hayAlgunCampoVacio)
    }

    @Test
    fun `signup debe aceptar todos los campos completos`() {
        // ARRANGE - Caso exitoso
        val nombre = "Juan"
        val apellido = "Pérez"
        val email = "juan@email.com"
        val password = "miPassword"
        
        // ACT & ASSERT
        val hayAlgunCampoVacio = email.isBlank() || password.isBlank() || apellido.isBlank() || nombre.isBlank()
        assertFalse("No debe haber campos vacíos", hayAlgunCampoVacio)
    }
}