package com.antaedo_karfuentealba.eva3_ambientefest.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun ValoracionEstrellas(
    modifier: Modifier = Modifier,
    valoracion: Double,
    tintColor: Color = Color(0xFF6B6B47) // Color marengo
) {
    val estrellasLlenas = floor(valoracion).toInt()
    val mediaEstrella = ceil(valoracion).toInt() > estrellasLlenas
    val estrellasVacias = 5 - estrellasLlenas - if (mediaEstrella) 1 else 0

    Row(modifier = modifier) {
        // Estrellas llenas
        repeat(estrellasLlenas) {
            Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = tintColor)
        }

        // Media estrella
        if (mediaEstrella) {
            Icon(imageVector = Icons.Filled.StarHalf, contentDescription = null, tint = tintColor)
        }

        // Estrellas vacías
        repeat(estrellasVacias) {
            Icon(imageVector = Icons.Outlined.StarOutline, contentDescription = null, tint = tintColor)
        }
    }
}
