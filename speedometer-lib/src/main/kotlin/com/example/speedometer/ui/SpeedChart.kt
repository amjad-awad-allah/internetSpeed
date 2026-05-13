package com.example.speedometer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun SpeedChart(
    points: List<Float>,
    maxSpeed: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.height(100.dp).fillMaxWidth()) {
        if (points.size < 2) return@Canvas

        val path = Path()
        val width = size.width
        val height = size.height
        val xIncrement = width / (points.size - 1)

        points.forEachIndexed { index, speed ->
            val x = index * xIncrement
            val y = height - (speed / maxSpeed).coerceIn(0f, 1f) * height
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        // Draw the line with a glowing gradient
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(listOf(Color(0xFF00F2FE), Color(0xFF4FACFE))),
            style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw semi-transparent area under the path
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                listOf(Color(0xFF4FACFE).copy(alpha = 0.2f), Color.Transparent)
            )
        )
    }
}
