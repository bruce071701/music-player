package com.app.musicplayer.feature.equalizer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.penji.musicplayer.offline.R
import com.app.musicplayer.core.media.AudioEqualizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: EqualizerViewModel = hiltViewModel()
) {
    val isEnabled by viewModel.isEnabled.collectAsState()
    val bandGains by viewModel.bandGains.collectAsState()
    val preamp by viewModel.preamp.collectAsState()
    val presets by viewModel.presets.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
    ) {
        // Top bar
        TopAppBar(
            title = { Text(stringResource(R.string.settings_equalizer), color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
            },
            actions = {
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { viewModel.setEnabled(it) }
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        // Preset row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = selectedPreset,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Preset", color = Color.White.copy(alpha = 0.6f)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    singleLine = true
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    presets.forEach { preset ->
                        DropdownMenuItem(
                            text = { Text(preset) },
                            onClick = { viewModel.selectPreset(preset); expanded = false }
                        )
                    }
                }
            }
            OutlinedButton(onClick = { viewModel.selectPreset("Flat") },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                Text("Reset", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Frequency response curve
        FrequencyResponseCurve(
            bandGains = bandGains,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Vertical fader sliders
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Preamp fader
            VerticalFader(
                value = preamp,
                onValueChange = { viewModel.setPreamp(it) },
                label = "Pre",
                valueLabel = String.format("%.1f", preamp),
                color = Color(0xFFFFFFFF),
                enabled = isEnabled
            )

            // 10 band faders
            bandGains.forEachIndexed { index, gain ->
                val freq = AudioEqualizer.CENTER_FREQUENCIES[index]
                val label = if (freq >= 1000) "${freq / 1000}K" else "$freq"
                val color = when {
                    gain > 2 -> Color(0xFF4CAF50)   // Green for boost
                    gain < -2 -> Color(0xFFFF5252)  // Red for cut
                    else -> Color(0xFFFFEB3B)       // Yellow neutral
                }
                VerticalFader(
                    value = gain,
                    onValueChange = { viewModel.setBandGain(index, it) },
                    label = label,
                    valueLabel = String.format("%.1f", gain),
                    color = color,
                    enabled = isEnabled
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun VerticalFader(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    valueLabel: String,
    color: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val normalizedValue = ((value + 12f) / 24f).coerceIn(0f, 1f)
    var boxHeight by remember { mutableFloatStateOf(1f) }

    Column(
        modifier = modifier.width(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Value display
        Text(
            text = valueLabel,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = if (enabled) color else Color.White.copy(alpha = 0.3f),
            textAlign = TextAlign.Center
        )

        // Vertical slider area
        Box(
            modifier = Modifier
                .weight(1f)
                .width(32.dp)
                .padding(vertical = 4.dp)
                .onSizeChanged { boxHeight = it.height.toFloat().coerceAtLeast(1f) }
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val position = event.changes.firstOrNull()?.position ?: continue
                            if (event.changes.any { it.pressed }) {
                                event.changes.forEach { it.consume() }
                                val y = position.y.coerceIn(0f, boxHeight)
                                val newNormalized = 1f - (y / boxHeight)
                                val newValue = (newNormalized * 24f - 12f).coerceIn(-12f, 12f)
                                onValueChange(newValue)
                            }
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cx = w / 2
                val trackWidth = 4.dp.toPx()
                val thumbWidth = 22.dp.toPx()
                val thumbHeight = 8.dp.toPx()

                // Track background
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.15f),
                    topLeft = Offset(cx - trackWidth / 2, 0f),
                    size = androidx.compose.ui.geometry.Size(trackWidth, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                )

                // Center line (0 dB marker)
                drawLine(
                    color = Color.White.copy(alpha = 0.2f),
                    start = Offset(cx - 6.dp.toPx(), h / 2),
                    end = Offset(cx + 6.dp.toPx(), h / 2),
                    strokeWidth = 1f
                )

                // Filled portion from center to current value
                val centerY = h / 2
                val thumbY = h * (1f - normalizedValue)
                val fillColor = if (enabled) color else Color.White.copy(alpha = 0.3f)

                if (normalizedValue > 0.5f) {
                    // Boost: fill from center upward
                    drawRoundRect(
                        color = fillColor,
                        topLeft = Offset(cx - trackWidth / 2, thumbY),
                        size = androidx.compose.ui.geometry.Size(trackWidth, centerY - thumbY),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                    )
                } else if (normalizedValue < 0.5f) {
                    // Cut: fill from center downward
                    drawRoundRect(
                        color = fillColor,
                        topLeft = Offset(cx - trackWidth / 2, centerY),
                        size = androidx.compose.ui.geometry.Size(trackWidth, thumbY - centerY),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                    )
                }

                // Thumb
                val thumbColor = if (enabled) Color.White else Color.White.copy(alpha = 0.4f)
                drawRoundRect(
                    color = thumbColor,
                    topLeft = Offset(cx - thumbWidth / 2, thumbY - thumbHeight / 2),
                    size = androidx.compose.ui.geometry.Size(thumbWidth, thumbHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                )
            }
        }

        // Frequency label
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FrequencyResponseCurve(
    bandGains: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val midY = h / 2

        // Draw grid lines
        for (i in 0..4) {
            val y = h * i / 4
            drawLine(
                color = Color.White.copy(alpha = 0.1f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
        }

        // Draw center line (0 dB)
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(0f, midY),
            end = Offset(w, midY),
            strokeWidth = 1f
        )

        // Draw frequency response curve
        if (bandGains.isNotEmpty()) {
            val path = Path()
            val points = bandGains.mapIndexed { index, gain ->
                val x = w * index / (bandGains.size - 1).coerceAtLeast(1)
                val y = midY - (gain / 12f) * (h / 2)
                Offset(x, y)
            }

            if (points.isNotEmpty()) {
                path.moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val cx = (prev.x + curr.x) / 2
                    path.cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                }

                drawPath(
                    path = path,
                    color = Color(0xFF8B5CF6),
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}
