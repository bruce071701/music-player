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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Premium vinyl disc with realistic metallic rim, grooves, and red center label.
 * Matches the high-end music player UI with glowing effects.
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
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "discRotation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2
            val cy = h / 2
            val center = Offset(cx, cy)
            val outerRadius = minOf(w, h) * 0.48f

            // === Metallic outer rim ===
            val rimWidth = outerRadius * 0.06f
            // Dark metallic rim gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4A4A4A),
                        Color(0xFF2A2A2A),
                        Color(0xFF1A1A1A),
                        Color(0xFF0D0D0D)
                    ),
                    center = center,
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = center
            )

            // Rim highlight (top-left light source)
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF5A5A5A).copy(alpha = 0.4f),
                        Color.Transparent,
                        Color(0xFF1A1A1A).copy(alpha = 0.3f)
                    ),
                    start = Offset(cx - outerRadius, cy - outerRadius),
                    end = Offset(cx + outerRadius, cy + outerRadius)
                ),
                radius = outerRadius,
                center = center
            )

            // Rim edge ring
            drawCircle(
                color = Color(0xFF3D3D3D),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 1.5f)
            )

            val discRadius = outerRadius - rimWidth

            // === Main vinyl disc body (rotating) ===
            rotate(degrees = if (isPlaying) rotation else 0f, pivot = center) {
                // Base disc - very dark with subtle gradient
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E1E1E),
                            Color(0xFF141414),
                            Color(0xFF0C0C0C),
                            Color(0xFF080808),
                            Color(0xFF0C0C0C),
                            Color(0xFF141414)
                        ),
                        center = center,
                        radius = discRadius
                    ),
                    radius = discRadius,
                    center = center
                )

                // === Vinyl grooves - concentric rings ===
                val grooveStart = 0.32f  // Start after label
                val grooveEnd = 0.95f    // End before rim
                val numGrooves = 45

                for (i in 0..numGrooves) {
                    val fraction = grooveStart + (grooveEnd - grooveStart) * i / numGrooves.toFloat()
                    val r = discRadius * fraction
                    val alpha = when {
                        i % 8 == 0 -> 0.12f
                        i % 4 == 0 -> 0.07f
                        i % 2 == 0 -> 0.04f
                        else -> 0.025f
                    }
                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        radius = r,
                        center = center,
                        style = Stroke(width = 0.6f)
                    )
                }

                // === Light reflection on vinyl (subtle sweep) ===
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.White.copy(alpha = 0.015f),
                            Color.White.copy(alpha = 0.04f),
                            Color.White.copy(alpha = 0.06f),
                            Color.White.copy(alpha = 0.04f),
                            Color.White.copy(alpha = 0.015f),
                            Color.Transparent,
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

                // Secondary reflection (opposite side, dimmer)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.White.copy(alpha = 0.02f),
                            Color.White.copy(alpha = 0.035f),
                            Color.White.copy(alpha = 0.02f),
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

                // === Center label (red gradient) ===
                val labelRadius = discRadius * 0.28f

                // Label base - deep red gradient
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFCC3333),
                            Color(0xFFB82020),
                            Color(0xFF8B1515),
                            Color(0xFF5A0E0E)
                        ),
                        center = center,
                        radius = labelRadius
                    ),
                    radius = labelRadius,
                    center = center
                )

                // Label edge ring
                drawCircle(
                    color = Color(0xFF3A1010),
                    radius = labelRadius,
                    center = center,
                    style = Stroke(width = 1.2f)
                )

                // Inner ring on label
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = labelRadius * 0.7f,
                    center = center,
                    style = Stroke(width = 0.5f)
                )

                // === Spindle (center dot) ===
                val spindleRadius = discRadius * 0.022f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFEEEEEE),
                            Color(0xFFAAAAAA),
                            Color(0xFF666666)
                        ),
                        center = center,
                        radius = spindleRadius
                    ),
                    radius = spindleRadius,
                    center = center
                )
            }

            // === Inner rim edge (between rim and disc) ===
            drawCircle(
                color = Color(0xFF2A2A2A),
                radius = discRadius,
                center = center,
                style = Stroke(width = 1f)
            )
        }
    }
}
