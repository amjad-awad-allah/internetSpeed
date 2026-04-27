package com.example.internetspeed.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SpeedGauge(
    speed: Float,
    maxSpeed: Float = 500f,
    modifier: Modifier = Modifier
) {
    // Smooth animation for the needle/progress
    val animatedSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SpeedAnimation"
    )

    Box(
        modifier = modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = size.center
            val radius = size.minDimension / 2
            val strokeWidth = 15.dp.toPx()

            // 1. Draw Background Arc (Track)
            drawArc(
                color = Color.Gray.copy(alpha = 0.1f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 2. Draw Progress Arc with Gradient
            val progressSweep = (animatedSpeed / maxSpeed).coerceIn(0f, 1f) * 270f
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to Color(0xFF00F2FE),
                    0.5f to Color(0xFF4FACFE),
                    1.0f to Color(0xFF00F2FE)
                ),
                startAngle = 135f,
                sweepAngle = progressSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 3. Draw Inner Glow (Subtle)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF4FACFE).copy(alpha = 0.1f), Color.Transparent),
                    center = center,
                    radius = radius
                )
            )
        }

        // 4. Digital Text Display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%.1f", animatedSpeed),
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Mbps",
                fontSize = 18.sp,
                color = Color.LightGray,
                letterSpacing = 2.sp
            )
        }
    }
}
