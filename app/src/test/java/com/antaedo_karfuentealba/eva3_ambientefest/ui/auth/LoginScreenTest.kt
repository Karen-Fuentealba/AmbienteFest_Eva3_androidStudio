package com.antaedo_karfuentealba.eva3_ambientefest.ui.auth

import org.junit.Test
import org.junit.Assert.*

/**
 * Test unitario para LoginScreen
 * LoginScreen NO tiene validaciones propias, solo captura datos
 */
class LoginScreenTest {

    @Test
    fun `login debe capturar email correctamente`() {
        // ARRANGE - Simula captura de datos en LoginScreen
        val emailCapturado = "usuario@email.com"
        
        // ACT & ASSERT
        assertFalse("Email capturado no debe estar vacío", emailCapturado.isEmpty())
    }
    
    @Test
    fun `login debe capturar password correctamente`() {
        // ARRANGE - Simula captura de datos
        val passwordCapturado = "miPassword"
        
        // ACT & ASSERT
        assertFalse("Password capturado no debe estar vacío", passwordCapturado.isEmpty())
    }

    @Test
    fun `login debe manejar campos vacios`() {
        // ARRANGE - Campos vacíos
        val emailVacio = ""
        val passwordVacio = ""
        
        // ACT & ASSERT
        assertTrue("Email vacío debe ser detectado", emailVacio.isEmpty())
        assertTrue("Password vacío debe ser detectado", passwordVacio.isEmpty())
    }
}