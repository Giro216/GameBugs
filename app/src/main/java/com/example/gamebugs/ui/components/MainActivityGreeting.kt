package com.example.gamebugs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainActivityGreeting(
    name: String,
    modifier: Modifier = Modifier,
    fontSizeSp: Int = 36,
    rotationDegrees: Float = -20f,
    textColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hello $name!",
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier
                .graphicsLayer(rotationZ = rotationDegrees)
                .background(Color.Cyan)
                .shadow(20.dp)
        )
    }
}