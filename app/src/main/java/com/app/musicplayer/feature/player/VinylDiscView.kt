package com.app.musicplayer.feature.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

/**
 * Elegant vinyl disc that rotates slowly when playing.
 * Clean, minimal, no tonearm — just the record spinning.
 */
@Composable
fun VinylDiscView(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "discRotation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2
            val cy = h / 2
            val center = Offset(cx, cy)
            val discRadius = minOf(w, h) * 0.44f

            // Outer glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF8B5CF6).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = discRadius * 1.15f
                ),
                radius = discRadius * 1.15f,
                center = center
            )

            // Rotating disc
            rotate(degrees = if (isPlaying) rotation else 0f, pivot = center) {
                // Main disc body
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1C1C28),
                            Color(0xFF101018),
                            Color(0xFF0A0A10),
                            Color(0xFF101018)
                        ),
                        center = center,
                        radius = discRadius
                    ),
                    radius = discRadius,
                    center = center
                )

                // Grooves
                for (i in 6..30) {
                    val r = discRadius * i / 32f
                    val alpha = when {
                        i % 6 == 0 -> 0.08f
                        i % 3 == 0 -> 0.04f
                        else -> 0.02f
                    }
                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        radius = r,
                        center = center,
                        style = Stroke(width = 0.4f)
                    )
                }

                // Light reflection
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.White.copy(alpha = 0.03f),
                            Color.White.copy(alpha = 0.07f),
                            Color.White.copy(alpha = 0.03f),
                            Color.Transparent,
                            Color.Transparent
                        ),
                        center = center
                    ),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = true,
                    topLeft = Offset(cx - discRadius, cy - discRadius),
                    size = Size(discRadius * 2, discRadius * 2)
                )

                // Center label
                val labelRadius = discRadius * 0.28f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD4783C),
                            Color(0xFFC4502A),
                            Color(0xFF8B2010)
                        ),
                        center = center,
                        radius = labelRadius
                    ),
                    radius = labelRadius,
                    center = center
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.15f),
                    radius = labelRadius,
                    center = center,
                    style = Stroke(width = 0.8f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = labelRadius * 0.55f,
                    center = center,
                    style = Stroke(width = 0.5f)
                )

                // Spindle
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFCCCCCC), Color(0xFF666666)),
                        center = center,
                        radius = discRadius * 0.025f
                    ),
                    radius = discRadius * 0.025f,
                    center = center
                )
            }

            // Disc edge ring
            drawCircle(
                color = Color(0xFF3A3A4C).copy(alpha = 0.4f),
                radius = discRadius,
                center = center,
                style = Stroke(width = 1f)
            )
        }
    }
}
