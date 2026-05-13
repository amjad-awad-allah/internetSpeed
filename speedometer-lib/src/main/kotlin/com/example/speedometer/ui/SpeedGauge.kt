package com.example.speedometer.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

enum class GaugeStyle {
    MODERN_ARC,
    GLOWING_DOTS,
    MINIMALIST_PULSE
}

@Composable
fun SpeedGauge(
    speed: Float,
    maxSpeed: Float = 500f,
    style: GaugeStyle = GaugeStyle.MODERN_ARC,
    primaryColor: Color = Color(0xFF00F2FE),
    secondaryColor: Color = Color(0xFF4FACFE),
    textColor: Color = Color.White,
    strokeWidth: Float = 12f,
    valueFontSize: Int = 54,
    animationDuration: Int = 1000,
    modifier: Modifier = Modifier
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
        label = "SpeedAnimation"
    )

    // Pulse animation for MINIMALIST_PULSE style
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
        label = "PulseAlpha"
    )

    Box(modifier = modifier.size(300.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = size.center
            val radius = size.minDimension / 2
            val actualStrokeWidth = strokeWidth.dp.toPx()

            when (style) {
                GaugeStyle.MODERN_ARC -> {
                    drawArc(
                        color = Color.Gray.copy(alpha = 0.1f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = actualStrokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(listOf(primaryColor, secondaryColor)),
                        startAngle = 135f,
                        sweepAngle = (animatedSpeed / maxSpeed).coerceIn(0f, 1f) * 270f,
                        useCenter = false,
                        style = Stroke(width = actualStrokeWidth, cap = StrokeCap.Round)
                    )
                }

                GaugeStyle.GLOWING_DOTS -> {
                    val dotCount = 40
                    for (i in 0 until dotCount) {
                        val angleInDegrees = 135f + (i.toFloat() / dotCount) * 270f
                        val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
                        val x = center.x + (radius - 20) * cos(angleInRadians).toFloat()
                        val y = center.y + (radius - 20) * sin(angleInRadians).toFloat()
                        
                        val isLit = (i.toFloat() / dotCount) <= (animatedSpeed / maxSpeed)
                        drawCircle(
                            color = if (isLit) primaryColor else Color.Gray.copy(alpha = 0.2f),
                            radius = if (isLit) 6f else 4f,
                            center = Offset(x, y),
                            alpha = if (isLit) 1f else 0.3f
                        )
                    }
                }

                GaugeStyle.MINIMALIST_PULSE -> {
                    drawCircle(
                        color = secondaryColor,
                        radius = radius * (animatedSpeed / maxSpeed).coerceIn(0.1f, 1f),
                        alpha = pulseAlpha * 0.2f,
                        style = Stroke(width = 4f)
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = 135f,
                        sweepAngle = (animatedSpeed / maxSpeed).coerceIn(0f, 1f) * 270f,
                        useCenter = false,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = String.format("%.1f", animatedSpeed), fontSize = valueFontSize.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(text = "Mbps", fontSize = (valueFontSize / 3).sp, color = textColor.copy(alpha = 0.7f))
        }
    }
}
