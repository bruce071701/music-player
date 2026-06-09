package com.app.musicplayer.feature.scan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.musicplayer.feature.library.LibraryViewModel
import com.app.musicplayer.feature.library.scanner.ScanProgress
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanMusicScreen(
    libraryViewModel: LibraryViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val scanProgress by libraryViewModel.scanProgress.collectAsState()
    var selectedFolder by remember { mutableStateOf<String?>(null) }
    var selectedFolderUri by remember { mutableStateOf<Uri?>(null) }
    var minDuration by remember { mutableIntStateOf(30) } // seconds
    var hasStartedScan by remember { mutableStateOf(false) }
    var minSize by remember { mutableIntStateOf(50) } // KB

    val context = androidx.compose.ui.platform.LocalContext.current

    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFolderUri = uri
            selectedFolder = DocumentFile.fromTreeUri(context, uri)?.name ?: uri.lastPathSegment
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Scan Music",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Scanning animation
            ScanGauge(isScanning = scanProgress.isScanning)

            Spacer(modifier = Modifier.height(24.dp))

            // Progress info
            if (scanProgress.isScanning) {
                LinearProgressIndicator(
                    progress = {
                        if (scanProgress.totalCount > 0) scanProgress.scannedCount.toFloat() / scanProgress.totalCount.toFloat() else 0f
                    },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${scanProgress.scannedCount} / ${scanProgress.totalCount} tracks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (scanProgress.lastScanResult >= 0) {
                Text(
                    "${scanProgress.lastScanResult} tracks found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Select folder
            Text(
                text = if (selectedFolder != null) "Folder: $selectedFolder" else "Select Folder",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { folderPicker.launch(null) }
                    .padding(vertical = 8.dp)
            )
            Text(
                text = "(Leave empty to scan all folders)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Min duration filter
            Text(
                "Ignore tracks shorter than",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            DurationOptions(selected = minDuration, onSelect = { minDuration = it })

            Spacer(modifier = Modifier.height(20.dp))

            // Min size filter
            Text(
                "Ignore files smaller than",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            SizeOptions(selected = minSize, onSelect = { minSize = it })

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Scan button / Back button
        if (hasStartedScan && !scanProgress.isScanning && scanProgress.lastScanResult >= 0) {
            // Scan finished - show result and back button
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${scanProgress.lastScanResult} tracks found",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text("Done", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Button(
                onClick = {
                    if (!scanProgress.isScanning) {
                        hasStartedScan = true
                        libraryViewModel.scanWithOptions(minDuration, minSize)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(26.dp),
                enabled = !scanProgress.isScanning
            ) {
                Text(
                    if (scanProgress.isScanning) "Scanning..." else "Scan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ScanGauge(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "needle"
    )

    Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val cx = size.width / 2
            val cy = size.height / 2
            val radius = size.width * 0.4f
            val primary = Color(0xFFE53935)

            // Outer ring
            drawCircle(
                color = primary.copy(alpha = 0.3f),
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(width = 8f)
            )

            // Inner glow
            drawCircle(
                color = primary.copy(alpha = 0.1f),
                radius = radius * 0.85f,
                center = Offset(cx, cy)
            )

            // Tick marks
            for (i in 0 until 60) {
                val angle = Math.toRadians((i * 6).toDouble())
                val inner = if (i % 5 == 0) radius * 0.75f else radius * 0.85f
                val outer = radius * 0.95f
                drawLine(
                    color = primary.copy(alpha = if (i % 5 == 0) 0.6f else 0.2f),
                    start = Offset(cx + inner * cos(angle).toFloat(), cy + inner * sin(angle).toFloat()),
                    end = Offset(cx + outer * cos(angle).toFloat(), cy + outer * sin(angle).toFloat()),
                    strokeWidth = if (i % 5 == 0) 2f else 1f
                )
            }

            // Needle (rotates when scanning)
            val needleAngle = if (isScanning) rotation else 0f
            rotate(needleAngle, Offset(cx, cy)) {
                drawLine(
                    color = primary,
                    start = Offset(cx, cy),
                    end = Offset(cx, cy - radius * 0.7f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }

            // Center dot
            drawCircle(
                color = primary,
                radius = 6f,
                center = Offset(cx, cy)
            )
        }
    }
}

@Composable
private fun DurationOptions(selected: Int, onSelect: (Int) -> Unit) {
    val options = listOf(10, 30, 60)
    Column {
        options.forEach { seconds ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(seconds) }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == seconds,
                    onClick = { onSelect(seconds) },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text(
                    "$seconds sec",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun SizeOptions(selected: Int, onSelect: (Int) -> Unit) {
    val options = listOf(0, 50, 100, 500)
    Column {
        options.forEach { kb ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(kb) }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == kb,
                    onClick = { onSelect(kb) },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text(
                    if (kb == 0) "No limit" else "$kb KB",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
