package com.example.internetspeed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp

@Composable
fun CompatibilitySection(speed: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompatibilityItem("4K Video", speed >= 25f, Modifier.weight(1f))
        CompatibilityItem("HD Video", speed >= 5f, Modifier.weight(1f))
        CompatibilityItem("Gaming", speed >= 15f, Modifier.weight(1f))
    }
}

@Composable
fun CompatibilityItem(label: String, isSupported: Boolean, modifier: Modifier = Modifier) {
    val color = if (isSupported) Color(0xFF00C853) else Color(0xFFFF5252)
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isSupported) "READY" else "SLOW",
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
