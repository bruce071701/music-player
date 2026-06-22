package com.app.musicplayer.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Generates a unique, visually appealing cover art based on the song title.
 * Uses procedural generation to create different abstract patterns.
 * Each song gets a distinct, repeatable visual based on its title hash.
 */
@Composable
fun GenerativeCoverArt(
    seed: String,
    modifier: Modifier = Modifier
) {
    val hash = seed.hashCode()
    val style = abs(hash) % 6

    Canvas(modifier = modifier.fillMaxSize()) {
        when (style) {
            0 -> drawNeonWaves(hash)
            1 -> drawRadialBurst(hash)
            2 -> drawGeometricMosaic(hash)
            3 -> drawAuroraGlow(hash)
            4 -> drawSoundWaveCircle(hash)
            else -> drawAbstractOrbs(hash)
        }
    }
}

// === Style 0: Neon wave lines over dark gradient ===
private fun DrawScope.drawNeonWaves(hash: Int) {
    val w = size.width
    val h = size.height
    val hue = abs(hash % 360)

    // Background
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0A0015), Color(0xFF150020), Color(0xFF05000A))
        )
    )

    // Multiple wave lines
    val colors = listOf(
        Color.hsv(hue.toFloat(), 0.8f, 0.9f),
        Color.hsv((hue + 40) % 360f, 0.7f, 0.8f),
        Color.hsv((hue + 80) % 360f, 0.6f, 0.7f)
    )

    for (i in 0..2) {
        val path = Path()
        val amplitude = h * 0.12f * (i + 1)
        val yOffset = h * (0.3f + i * 0.2f)
        val phase = (hash * (i + 1) % 100) / 100f * PI.toFloat() * 2

        path.moveTo(0f, yOffset)
        for (x in 0..w.toInt() step 3) {
            val xf = x.toFloat()
            val y = yOffset + sin(xf / w * PI.toFloat() * 3 + phase) * amplitude
            path.lineTo(xf, y)
        }

        drawPath(
            path = path,
            color = colors[i].copy(alpha = 0.7f - i * 0.15f),
            style = Stroke(width = (3f - i * 0.8f))
        )
    }

    // Glow circle
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(colors[0].copy(alpha = 0.2f), Color.Transparent),
            center = Offset(w * 0.7f, h * 0.3f),
            radius = w * 0.4f
        ),
        radius = w * 0.4f,
        center = Offset(w * 0.7f, h * 0.3f)
    )
}

// === Style 1: Radial light burst ===
private fun DrawScope.drawRadialBurst(hash: Int) {
    val w = size.width
    val h = size.height
    val hue = abs(hash % 360)

    // Dark background
    drawRect(color = Color(0xFF050508))

    val centerX = w * (0.3f + abs(hash % 40) / 100f)
    val centerY = h * (0.3f + abs((hash / 7) % 40) / 100f)
    val center = Offset(centerX, centerY)

    // Radial rays
    val rayCount = 12 + abs(hash % 8)
    val baseColor = Color.hsv(hue.toFloat(), 0.9f, 0.85f)

    for (i in 0 until rayCount) {
        val angle = (i.toFloat() / rayCount) * PI.toFloat() * 2
        val length = w * (0.6f + abs((hash * (i + 1)) % 30) / 100f)
        val endX = centerX + cos(angle) * length
        val endY = centerY + sin(angle) * length

        drawLine(
            color = baseColor.copy(alpha = 0.15f + abs((hash * i) % 20) / 100f),
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 2f + abs((hash * i) % 3)
        )
    }

    // Central glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                baseColor.copy(alpha = 0.6f),
                baseColor.copy(alpha = 0.2f),
                Color.Transparent
            ),
            center = center,
            radius = w * 0.25f
        ),
        radius = w * 0.25f,
        center = center
    )

    // Small secondary glow
    val secColor = Color.hsv((hue + 180) % 360f, 0.7f, 0.7f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(secColor.copy(alpha = 0.3f), Color.Transparent),
            center = Offset(w * 0.8f, h * 0.7f),
            radius = w * 0.2f
        ),
        radius = w * 0.2f,
        center = Offset(w * 0.8f, h * 0.7f)
    )
}

// === Style 2: Geometric mosaic / triangles ===
private fun DrawScope.drawGeometricMosaic(hash: Int) {
    val w = size.width
    val h = size.height
    val hue = abs(hash % 360)

    drawRect(color = Color(0xFF080810))

    val gridSize = 4
    val cellW = w / gridSize
    val cellH = h / gridSize

    for (row in 0 until gridSize) {
        for (col in 0 until gridSize) {
            val cellHash = hash * (row * gridSize + col + 1)
            val brightness = 0.3f + abs(cellHash % 50) / 100f
            val saturation = 0.5f + abs(cellHash % 40) / 100f
            val cellHue = (hue + abs(cellHash % 60) - 30) % 360

            val color = Color.hsv(cellHue.toFloat(), saturation, brightness)
            val x = col * cellW
            val y = row * cellH

            if (abs(cellHash) % 3 == 0) {
                // Triangle
                val path = Path()
                path.moveTo(x, y + cellH)
                path.lineTo(x + cellW / 2, y)
                path.lineTo(x + cellW, y + cellH)
                path.close()
                drawPath(path, color = color.copy(alpha = 0.6f), style = Fill)
            } else if (abs(cellHash) % 3 == 1) {
                // Rectangle
                drawRect(
                    color = color.copy(alpha = 0.5f),
                    topLeft = Offset(x + cellW * 0.1f, y + cellH * 0.1f),
                    size = Size(cellW * 0.8f, cellH * 0.8f)
                )
            } else {
                // Circle
                drawCircle(
                    color = color.copy(alpha = 0.5f),
                    radius = cellW * 0.35f,
                    center = Offset(x + cellW / 2, y + cellH / 2)
                )
            }
        }
    }

    // Overlay glow
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.hsv(hue.toFloat(), 0.6f, 0.5f).copy(alpha = 0.2f),
                Color.Transparent
            ),
            center = Offset(w / 2, h / 2),
            radius = w * 0.6f
        )
    )
}

// === Style 3: Aurora / Northern lights glow ===
private fun DrawScope.drawAuroraGlow(hash: Int) {
    val w = size.width
    val h = size.height
    val hue = abs(hash % 360)

    // Deep dark background
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF000008), Color(0xFF020010), Color(0xFF000005))
        )
    )

    // Aurora bands
    val bandCount = 3 + abs(hash % 3)
    for (i in 0 until bandCount) {
        val bandHue = (hue + i * 50) % 360
        val bandColor = Color.hsv(bandHue.toFloat(), 0.7f, 0.8f)
        val yCenter = h * (0.2f + i * 0.2f)
        val phase = (hash * (i + 1) % 100) / 50f * PI.toFloat()

        val path = Path()
        path.moveTo(0f, yCenter)
        for (x in 0..w.toInt() step 2) {
            val xf = x.toFloat()
            val y = yCenter + sin(xf / w * PI.toFloat() * 2 + phase) * h * 0.08f
            path.lineTo(xf, y)
        }
        path.lineTo(w, h)
        path.lineTo(0f, h)
        path.close()

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(
                    bandColor.copy(alpha = 0.25f - i * 0.05f),
                    bandColor.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                startY = yCenter - h * 0.1f,
                endY = yCenter + h * 0.3f
            )
        )
    }

    // Star-like dots
    val starCount = 15 + abs(hash % 10)
    for (i in 0 until starCount) {
        val sx = abs((hash * (i + 3)) % w.toInt()).toFloat()
        val sy = abs((hash * (i + 7)) % (h * 0.5f).toInt()).toFloat()
        val sr = 1f + abs((hash * i) % 2)
        drawCircle(
            color = Color.White.copy(alpha = 0.4f + abs((hash * i) % 30) / 100f),
            radius = sr,
            center = Offset(sx, sy)
        )
    }
}

// === Style 4: Concentric sound wave circles ===
private fun DrawScope.drawSoundWaveCircle(hash: Int) {
    val w = size.width
    val h = size.height
    val hue = abs(hash % 360)
    val center = Offset(w / 2, h / 2)

    // Background
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF0A0A15), Color(0xFF050508)),
            center = center,
            radius = w * 0.7f
        )
    )

    val baseColor = Color.hsv(hue.toFloat(), 0.8f, 0.8f)
    val ringCount = 8 + abs(hash % 5)

    for (i in 0 until ringCount) {
        val radius = w * 0.08f * (i + 1)
        val alpha = 0.6f - i * 0.05f
        val strokeW = 2f + abs((hash * (i + 1)) % 3)

        // Vary radius for each ring to create irregular waveform feel
        val variation = abs((hash * (i + 2)) % 15) / 100f
        drawCircle(
            color = baseColor.copy(alpha = alpha.coerceAtLeast(0.1f)),
            radius = radius * (1f + variation),
            center = center,
            style = Stroke(width = strokeW)
        )
    }

    // Central dot glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(baseColor, baseColor.copy(alpha = 0.3f), Color.Transparent),
            center = center,
            radius = w * 0.12f
        ),
        radius = w * 0.12f,
        center = center
    )
}

// === Style 5: Abstract floating orbs ===
private fun DrawScope.drawAbstractOrbs(hash: Int) {
    val w = size.width
    val h = size.height
    val hue = abs(hash % 360)

    // Background gradient
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF08000F), Color(0xFF0F0015), Color(0xFF050008)),
            start = Offset.Zero,
            end = Offset(w, h)
        )
    )

    // Multiple glowing orbs
    val orbCount = 4 + abs(hash % 3)
    for (i in 0 until orbCount) {
        val orbHue = (hue + i * 70) % 360
        val orbColor = Color.hsv(orbHue.toFloat(), 0.7f, 0.7f)
        val cx = abs((hash * (i + 1)) % w.toInt()).toFloat()
        val cy = abs((hash * (i + 3)) % h.toInt()).toFloat()
        val radius = w * (0.15f + abs((hash * i) % 20) / 100f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    orbColor.copy(alpha = 0.4f),
                    orbColor.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = radius
            ),
            radius = radius,
            center = Offset(cx, cy)
        )
    }

    // Mesh-like connecting lines between orbs
    val points = List(orbCount) { i ->
        Offset(
            abs((hash * (i + 1)) % w.toInt()).toFloat(),
            abs((hash * (i + 3)) % h.toInt()).toFloat()
        )
    }
    for (i in points.indices) {
        for (j in i + 1 until points.size) {
            drawLine(
                color = Color.White.copy(alpha = 0.06f),
                start = points[i],
                end = points[j],
                strokeWidth = 0.8f
            )
        }
    }
}
