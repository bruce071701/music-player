package com.app.musicplayer.feature.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.musicplayer.core.ui.theme.AccentPrimary
import com.app.musicplayer.core.ui.theme.AppBackground
import com.app.musicplayer.core.ui.theme.AppSurfaceVariant
import com.app.musicplayer.core.ui.theme.TextPrimary
import com.app.musicplayer.core.ui.theme.TextSecondary
import com.app.musicplayer.core.ui.theme.TextTertiary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    viewModel: BackupViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isBackingUp by remember { mutableStateOf(false) }
    var isRestoring by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Launcher for creating backup file (save to Google Drive / local)
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null) {
            isBackingUp = true
            statusMessage = "Packing music files into backup..."
            scope.launch {
                try {
                    val success = viewModel.exportBackup(context, uri, includeMusicFiles = true)
                    statusMessage = if (success) "✓ Backup saved successfully! Check your selected location." else "✗ Backup failed - could not write file"
                } catch (e: Exception) {
                    statusMessage = "✗ Backup failed: ${e.message}"
                } finally {
                    isBackingUp = false
                }
            }
        } else {
            statusMessage = "Backup cancelled"
        }
    }

    // Launcher for metadata-only backup (playlists, favorites, settings)
    val metadataBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            isBackingUp = true
            scope.launch {
                try {
                    val success = viewModel.exportMetadataOnly(context, uri)
                    statusMessage = if (success) "Settings backup saved!" else "Backup failed"
                } catch (e: Exception) {
                    statusMessage = "Backup failed: ${e.message}"
                } finally {
                    isBackingUp = false
                }
            }
        }
    }

    // Launcher for picking backup file to restore (from Google Drive / local)
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            isRestoring = true
            statusMessage = "Reading backup file..."
            scope.launch {
                try {
                    val result = viewModel.importBackup(context, uri)
                    statusMessage = if (result > 0) {
                        "✓ Restored $result playlists! Rescan your library to find restored music files."
                    } else {
                        "No playlists found in backup. Music files may still have been restored."
                    }
                } catch (e: Exception) {
                    statusMessage = "✗ Restore failed: ${e.message}"
                } finally {
                    isRestoring = false
                }
            }
        } else {
            statusMessage = "Restore cancelled"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = {
                Text(
                    "Backup & Restore",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppSurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            "What gets backed up:",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "• Playlists and track assignments\n• Favorites list\n• Play counts and history\n• Equalizer presets\n• App settings",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Backup with music files
            ActionCard(
                icon = Icons.Default.CloudUpload,
                title = "Backup Music + Settings",
                subtitle = "Upload all music files to Google Drive or local storage",
                buttonText = if (isBackingUp) "Backing up..." else "Backup All",
                isLoading = isBackingUp,
                accentColor = Color(0xFF4CAF50),
                onClick = {
                    if (!isBackingUp) {
                        backupLauncher.launch("music_backup_${System.currentTimeMillis()}.zip")
                    }
                }
            )

            // Settings only backup
            ActionCard(
                icon = Icons.Default.Backup,
                title = "Backup Settings Only",
                subtitle = "Playlists, favorites, EQ presets (small file)",
                buttonText = if (isBackingUp) "Backing up..." else "Backup Settings",
                isLoading = isBackingUp,
                accentColor = Color(0xFFFF8A65),
                onClick = {
                    if (!isBackingUp) {
                        metadataBackupLauncher.launch("music_settings_backup.json")
                    }
                }
            )

            // Restore button
            ActionCard(
                icon = Icons.Default.CloudDownload,
                title = "Restore from Backup",
                subtitle = "Pick your backup file from Google Drive or local storage",
                buttonText = if (isRestoring) "Restoring..." else "Select Backup File",
                isLoading = isRestoring,
                accentColor = Color(0xFF42A5F5),
                onClick = {
                    if (!isRestoring) {
                        restoreLauncher.launch(arrayOf("application/json", "application/zip", "application/octet-stream", "*/*"))
                    }
                }
            )

            // Status message
            statusMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.contains("success") || message.contains("Restored"))
                            Color(0xFF1B5E20).copy(alpha = 0.3f)
                        else Color(0xFF4A1010).copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // How it works
            Text(
                "How it works",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary
            )
            Text(
                "• \"Backup All\" packs your music files + settings into a zip file and saves it to Google Drive or local storage.\n\n" +
                "• \"Backup Settings\" saves only playlists, favorites, and EQ presets (very small file).\n\n" +
                "• \"Restore\" reads either type of backup file and restores your data. If it's a full backup with music, the files are extracted to your device.\n\n" +
                "Tip: Make sure you have the Google Drive app installed for cloud backup.",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    buttonText: String,
    isLoading: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(22.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
