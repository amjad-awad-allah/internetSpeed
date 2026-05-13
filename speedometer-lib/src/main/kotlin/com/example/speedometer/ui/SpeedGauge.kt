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
    MINIMALIST_PULSE,
    NEON_LINES,
    SHARP_NEEDLE
}

@Composable
fun SpeedGauge(
    speed: Float,
    maxSpeed: Float = 500f,
    isTesting: Boolean = false,
    style: GaugeStyle = GaugeStyle.MODERN_ARC,
    primaryColor: Color = Color(0xFF00F2FE),
    secondaryColor: Color = Color(0xFF4FACFE),
    turboColor: Color = Color(0xFFFF4E50),
    textColor: Color = Color.White,
    strokeWidth: Float = 12f,
    valueFontSize: Int = 54,
    animationSpec: AnimationSpec<Float> = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
    modifier: Modifier = Modifier
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = animationSpec,
        label = "SpeedAnimation"
    )

    // Vibration animation for "Testing" state
    val infiniteTransition = rememberInfiniteTransition(label = "GaugeEffects")
    val vibrateX by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(animation = tween(50, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "VibrationX"
    )
    val vibrateY by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(animation = tween(40, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "VibrationY"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
        label = "PulseAlpha"
    )

    // Dynamic Color Shift based on speed percentage
    val currentProgress = (animatedSpeed / maxSpeed).coerceIn(0f, 1f)
    val dynamicColor = lerp(primaryColor, turboColor, currentProgress)

    Box(
        modifier = modifier
            .size(300.dp)
            .graphicsLayer {
                if (isTesting && animatedSpeed > 10f) {
                    translationX = vibrateX * (animatedSpeed / maxSpeed) * 5f
                    translationY = vibrateY * (animatedSpeed / maxSpeed) * 5f
                }
            },
        contentAlignment = Alignment.Center
    ) {
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
                    
                    // Outer Glow
                    drawArc(
                        brush = Brush.sweepGradient(listOf(dynamicColor, secondaryColor)),
                        startAngle = 135f,
                        sweepAngle = (animatedSpeed / maxSpeed).coerceIn(0f, 1f) * 270f,
                        useCenter = false,
                        style = Stroke(width = actualStrokeWidth + 10f, cap = StrokeCap.Round),
                        alpha = 0.2f
                    )

                    // Main Arc
                    drawArc(
                        brush = Brush.sweepGradient(listOf(dynamicColor, secondaryColor)),
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
                            color = if (isLit) dynamicColor else Color.Gray.copy(alpha = 0.2f),
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
                        color = dynamicColor,
                        startAngle = 135f,
                        sweepAngle = (animatedSpeed / maxSpeed).coerceIn(0f, 1f) * 270f,
                        useCenter = false,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                }

                GaugeStyle.NEON_LINES -> {
                    val lineCount = 60
                    for (i in 0 until lineCount) {
                        val angleInDegrees = 135f + (i.toFloat() / lineCount) * 270f
                        val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
                        
                        val isLit = (i.toFloat() / lineCount) <= (animatedSpeed / maxSpeed)
                        val startLen = radius - 40
                        val endLen = if (isLit) radius else radius - 15
                        
                        val startX = center.x + startLen * cos(angleInRadians).toFloat()
                        val startY = center.y + startLen * sin(angleInRadians).toFloat()
                        val endX = center.x + endLen * cos(angleInRadians).toFloat()
                        val endY = center.y + endLen * sin(angleInRadians).toFloat()

                        if (isLit) {
                            // Neon Glow Line
                            drawLine(
                                color = dynamicColor.copy(alpha = 0.3f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = 10f,
                                cap = StrokeCap.Round
                            )
                        }

                        drawLine(
                            color = if (isLit) dynamicColor else Color.Gray.copy(alpha = 0.2f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (isLit) 4f else 2f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                GaugeStyle.SHARP_NEEDLE -> {
                    // Static background arc
                    drawArc(
                        color = Color.Gray.copy(alpha = 0.1f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 2f)
                    )
                    
                    // Motion Blur / Trail
                    val trailAngle = 135f + ((animatedSpeed * 0.95f) / maxSpeed).coerceIn(0f, 1f) * 270f
                    val trailRad = Math.toRadians(trailAngle.toDouble())
                    drawLine(
                        color = dynamicColor.copy(alpha = 0.3f),
                        start = center,
                        end = Offset(
                            center.x + (radius - 15) * cos(trailRad).toFloat(),
                            center.y + (radius - 15) * sin(trailRad).toFloat()
                        ),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )

                    // The needle
                    val needleAngle = 135f + (animatedSpeed / maxSpeed).coerceIn(0f, 1f) * 270f
                    val needleRad = Math.toRadians(needleAngle.toDouble())
                    val needleEnd = Offset(
                        center.x + (radius - 10) * cos(needleRad).toFloat(),
                        center.y + (radius - 10) * sin(needleRad).toFloat()
                    )
                    
                    drawLine(
                        brush = Brush.linearGradient(listOf(dynamicColor, secondaryColor)),
                        start = center,
                        end = needleEnd,
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )
                    
                    drawCircle(color = dynamicColor, radius = 12f, center = center)
                }
            }

            // --- Testing Phase Special Effects ---
            if (isTesting) {
                // Draw "Speed Lines" around the gauge
                val lineCount = 12
                val lineProgress by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(animation = tween(500, easing = LinearEasing)),
                    label = "SpeedLines"
                )

                for (i in 0 until lineCount) {
                    val angle = (i.toFloat() / lineCount) * 360f
                    val rad = Math.toRadians(angle.toDouble())
                    val startDist = radius + 20 + (lineProgress * 30)
                    val endDist = startDist + 20
                    
                    drawLine(
                        color = dynamicColor.copy(alpha = (1f - lineProgress) * 0.5f),
                        start = Offset(
                            center.x + startDist * cos(rad).toFloat(),
                            center.y + startDist * sin(rad).toFloat()
                        ),
                        end = Offset(
                            center.x + endDist * cos(rad).toFloat(),
                            center.y + endDist * sin(rad).toFloat()
                        ),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
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
