package com.antaedo_karfuentealba.eva3_ambientefest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.ColorPrincipal
import android.util.Log

@Composable
fun CartIconWithBadge(
    cartCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("CartIconWithBadge", "Rendering with cartCount: $cartCount")
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.ShoppingCart, 
                contentDescription = "Carrito",
                tint = ColorPrincipal
            )
        }
        
        if (cartCount > 0) {
            Badge(
                modifier = Modifier
                    .offset(x = (-4).dp, y = 4.dp)
                    .size(20.dp)
                    .clip(CircleShape),
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Text(
                    text = if (cartCount > 99) "99+" else cartCount.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}