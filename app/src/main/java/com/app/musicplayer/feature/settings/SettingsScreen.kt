package com.app.musicplayer.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.HeadsetOff
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.JoinLeft
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.penji.musicplayer.offline.R
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.musicplayer.feature.library.scanner.ScanProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNavigateToEqualizer: () -> Unit = {},
    onNavigateToTransfer: () -> Unit = {},
    onNavigateToBackup: () -> Unit = {},
    onNavigateToScan: () -> Unit = {}
) {
    val context = LocalContext.current
    val gaplessEnabled by viewModel.gaplessEnabled.collectAsState()
    val crossfadeDuration by viewModel.crossfadeDuration.collectAsState()
    val crossfadeEnabled = crossfadeDuration > 0
    val pauseOnHeadphone by viewModel.pauseOnHeadphone.collectAsState()
    val currentLanguage by viewModel.languageManager.currentLanguage.collectAsState(initial = "")
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val sleepTimerActive by viewModel.sleepTimerActive.collectAsState()
    val sleepTimerRemaining by viewModel.sleepTimerRemaining.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentCode = currentLanguage,
            onSelect = { code ->
                viewModel.setLanguage(code)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showSleepTimerDialog) {
        SleepTimerSettingsDialog(
            isActive = sleepTimerActive,
            remainingMs = sleepTimerRemaining,
            onTimerSet = { minutes ->
                viewModel.startSleepTimer(minutes)
                showSleepTimerDialog = false
            },
            onCancel = {
                viewModel.cancelSleepTimer()
                showSleepTimerDialog = false
            },
            onDismiss = { showSleepTimerDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            currentTheme = themeMode,
            onSelect = { mode ->
                viewModel.setThemeMode(mode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = {
                Text(stringResource(R.string.nav_settings), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        // === General Settings ===
        SectionHeader(stringResource(R.string.section_general))

        SettingsClickItem(
            icon = Icons.Default.ColorLens,
            iconTint = MaterialTheme.colorScheme.primary,
            title = stringResource(R.string.settings_theme),
            subtitle = stringResource(R.string.settings_theme_desc),
            trailingText = when (themeMode) {
                com.app.musicplayer.core.datastore.ThemeMode.LIGHT -> "Light"
                com.app.musicplayer.core.datastore.ThemeMode.DARK -> "Dark"
                com.app.musicplayer.core.datastore.ThemeMode.AMOLED -> "AMOLED"
                com.app.musicplayer.core.datastore.ThemeMode.PINK_ORANGE -> "Pink Orange"
                com.app.musicplayer.core.datastore.ThemeMode.SYSTEM -> "System"
            },
            onClick = { showThemeDialog = true }
        )
        SettingsClickItem(
            icon = Icons.Default.Share,
            iconTint = Color(0xFF42A5F5),
            title = stringResource(R.string.settings_transfer),
            subtitle = stringResource(R.string.settings_transfer_desc),
            onClick = onNavigateToTransfer
        )
        SettingsClickItem(
            icon = Icons.Default.Backup,
            iconTint = Color(0xFF66BB6A),
            title = stringResource(R.string.settings_backup),
            subtitle = stringResource(R.string.settings_backup_desc),
            onClick = onNavigateToBackup
        )
        SettingsToggleItem(
            icon = Icons.Default.HeadsetOff,
            title = stringResource(R.string.settings_pause_disconnect),
            subtitle = stringResource(R.string.settings_pause_disconnect_desc),
            checked = pauseOnHeadphone,
            onCheckedChange = { viewModel.setPauseOnHeadphone(it) }
        )
        SettingsClickItem(
            icon = Icons.Default.Language,
            iconTint = Color(0xFF26C6DA),
            title = stringResource(R.string.settings_language),
            trailingText = com.app.musicplayer.core.datastore.LanguageManager.SUPPORTED_LANGUAGES
                .find { it.code == currentLanguage }?.nativeName ?: "System",
            onClick = { showLanguageDialog = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // === Music ===
        SectionHeader(stringResource(R.string.section_music))

        SettingsClickItem(
            icon = Icons.Default.Refresh,
            iconTint = if (scanProgress.isScanning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            title = stringResource(R.string.settings_scan_music),
            subtitle = when {
                scanProgress.isScanning -> "${scanProgress.scannedCount} / ${scanProgress.totalCount}"
                scanProgress.lastScanResult >= 0 -> "${scanProgress.lastScanResult} tracks found"
                else -> null
            },
            onClick = { if (!scanProgress.isScanning) onNavigateToScan() }
        )
        if (scanProgress.isScanning) {
            androidx.compose.material3.LinearProgressIndicator(
                progress = {
                    if (scanProgress.totalCount > 0) scanProgress.scannedCount.toFloat() / scanProgress.totalCount.toFloat() else 0f
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(3.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        SettingsClickItem(
            icon = Icons.Default.Bedtime,
            iconTint = if (sleepTimerActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            title = stringResource(R.string.settings_sleep_timer),
            subtitle = if (sleepTimerActive) formatTimerRemaining(sleepTimerRemaining) else null,
            onClick = { showSleepTimerDialog = true }
        )
        SettingsClickItem(
            icon = Icons.Default.Equalizer,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            title = stringResource(R.string.settings_equalizer),
            onClick = onNavigateToEqualizer
        )
        SettingsToggleItem(
            icon = Icons.Default.SwapHoriz,
            title = stringResource(R.string.settings_crossfade),
            subtitle = stringResource(R.string.settings_crossfade_desc),
            checked = crossfadeEnabled,
            onCheckedChange = {
                if (it) viewModel.setCrossfade(3000) else viewModel.setCrossfade(0)
            }
        )
        SettingsToggleItem(
            icon = Icons.Default.JoinLeft,
            title = stringResource(R.string.settings_gapless),
            subtitle = stringResource(R.string.settings_gapless_desc),
            checked = gaplessEnabled,
            onCheckedChange = { viewModel.setGapless(it) }
        )
        SettingsClickItem(
            icon = Icons.Default.Widgets,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            title = stringResource(R.string.settings_widget),
            subtitle = stringResource(R.string.settings_widget_desc),
            onClick = {
                // Open system widget picker or pin widget
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(context)
                        val widgetProvider = android.content.ComponentName(
                            context,
                            com.app.musicplayer.feature.widget.MusicWidget4x2Receiver::class.java
                        )
                        if (appWidgetManager.isRequestPinAppWidgetSupported) {
                            appWidgetManager.requestPinAppWidget(widgetProvider, null, null)
                        }
                    }
                } catch (_: Exception) { }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // === Help ===
        SectionHeader(stringResource(R.string.section_help))

        SettingsClickItem(
            icon = Icons.Default.HelpOutline,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            title = stringResource(R.string.settings_faq),
            onClick = { }
        )
        SettingsClickItem(
            icon = Icons.Default.Star,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            title = stringResource(R.string.settings_rate),
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
                } catch (_: Exception) { }
            }
        )
        SettingsClickItem(
            icon = Icons.Default.Shield,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            title = "Privacy Policy",
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/mp3privacys/home")))
                } catch (_: Exception) { }
            }
        )
        SettingsClickItem(
            icon = Icons.Default.Description,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            title = "Terms of Service",
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/mp3-tos/home")))
                } catch (_: Exception) { }
            }
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
private fun SettingsClickItem(
    icon: ImageVector,
    iconTint: Color = Color(0xFFB0B0B0),
    title: String,
    subtitle: String? = null,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        }
        if (trailingText != null) {
            Text(trailingText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun LanguagePickerDialog(
    currentCode: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_language)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                com.app.musicplayer.core.datastore.LanguageManager.SUPPORTED_LANGUAGES.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(lang.code) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = lang.code == currentCode,
                            onClick = { onSelect(lang.code) },
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Column {
                            Text(lang.nativeName, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                            if (lang.nativeName != lang.displayName) {
                                Text(lang.displayName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
private fun SleepTimerSettingsDialog(
    isActive: Boolean,
    remainingMs: Long,
    onTimerSet: (Int) -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(15, 30, 45, 60, 90, 120)
    
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.sleep_timer)) },
        text = {
            Column {
                if (isActive) {
                    // Show active timer status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTimerRemaining(remainingMs),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        androidx.compose.material3.TextButton(onClick = onCancel) {
                            Text(stringResource(R.string.cancel_timer), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Text(
                    text = if (isActive) "Change timer:" else "Stop playback after:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                options.forEach { min ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTimerSet(min) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (min < 60) "$min min" else "${min / 60} h ${min % 60} min",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { 
                Text(stringResource(R.string.cancel)) 
            }
        }
    )
}

private fun formatTimerRemaining(ms: Long): String {
    if (ms <= 0) return ""
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d remaining".format(min, sec)
}

@Composable
private fun ThemePickerDialog(
    currentTheme: com.app.musicplayer.core.datastore.ThemeMode,
    onSelect: (com.app.musicplayer.core.datastore.ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val themes = listOf(
        com.app.musicplayer.core.datastore.ThemeMode.SYSTEM to "System",
        com.app.musicplayer.core.datastore.ThemeMode.LIGHT to "Light",
        com.app.musicplayer.core.datastore.ThemeMode.DARK to "Dark",
        com.app.musicplayer.core.datastore.ThemeMode.AMOLED to "AMOLED",
        com.app.musicplayer.core.datastore.ThemeMode.PINK_ORANGE to "Pink Orange"
    )

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_theme)) },
        text = {
            Column {
                themes.forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(mode) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = mode == currentTheme,
                            onClick = { onSelect(mode) },
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Column {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = when (mode) {
                                    com.app.musicplayer.core.datastore.ThemeMode.SYSTEM -> "Follow system settings"
                                    com.app.musicplayer.core.datastore.ThemeMode.LIGHT -> "Light background, dark text"
                                    com.app.musicplayer.core.datastore.ThemeMode.DARK -> "Dark background, light text"
                                    com.app.musicplayer.core.datastore.ThemeMode.AMOLED -> "Pure black for OLED screens"
                                    com.app.musicplayer.core.datastore.ThemeMode.PINK_ORANGE -> "Warm pink-orange gradient accent"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
