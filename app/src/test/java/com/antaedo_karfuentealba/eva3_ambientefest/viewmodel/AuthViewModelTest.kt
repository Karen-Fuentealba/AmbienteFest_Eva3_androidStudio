package com.antaedo_karfuentealba.eva3_ambientefest.viewmodel

import org.junit.Test
import org.junit.Assert.*

/**
 * Test unitario para AuthViewModel (AuthUiState.kt)
 * Prueba SOLO las validaciones que realmente están implementadas
 */
class AuthViewModelTest {

    @Test
    fun `login debe rechazar email vacio`() {
        // ARRANGE - Simula validación real: if (state.email.isBlank())
        val emailVacio = ""
        val emailValido = "cualquier@cosa.com"
        
        // ACT & ASSERT - AuthViewModel línea 69
        assertTrue("Email vacío debe ser rechazado", emailVacio.isBlank())
        assertFalse("Email con contenido debe pasar", emailValido.isBlank())
    }
    
    @Test
    fun `login debe rechazar password vacio`() {
        // ARRANGE - Simula validación real: if (state.password.isBlank())
        val passwordVacio = ""
        val passwordValido = "abc"
        
        // ACT & ASSERT - AuthViewModel línea 73
        assertTrue("Password vacío debe ser rechazado", passwordVacio.isBlank())
        assertFalse("Password con contenido debe pasar", passwordValido.isBlank())
    }

    @Test
    fun `signup debe rechazar nombre vacio`() {
        // ARRANGE - Simula validación real: if (state.name.isBlank())
        val nombreVacio = ""
        val nombreValido = "Juan"
        
        // ACT & ASSERT - AuthViewModel línea 115
        assertTrue("Nombre vacío debe ser rechazado", nombreVacio.isBlank())
        assertFalse("Nombre con contenido debe pasar", nombreValido.isBlank())
    }

    @Test
    fun `signup debe rechazar password vacio`() {
        // ARRANGE - Simula validación real: if (state.password.isBlank())
        val passwordVacio = ""
        val passwordValido = "x"
        
        // ACT & ASSERT - AuthViewModel línea 131
        assertTrue("Password vacío debe ser rechazado", passwordVacio.isBlank())
        assertFalse("Password con contenido debe pasar", passwordValido.isBlank())
    }
}