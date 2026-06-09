package com.app.musicplayer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.app.musicplayer.core.datastore.AppPreferences
import com.app.musicplayer.core.datastore.ThemeMode
import com.app.musicplayer.core.ui.theme.MusicPlayerTheme
import com.app.musicplayer.feature.main.MainScreen
import com.app.musicplayer.feature.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val hasSeenSplash = prefs.getBoolean("has_seen_splash", false)

        setContent {
            val themeMode by appPreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            var showSplash by remember { mutableStateOf(!hasSeenSplash) }

            MusicPlayerTheme(themeMode = themeMode) {
                if (showSplash) {
                    SplashScreen(
                        onFinished = {
                            prefs.edit().putBoolean("has_seen_splash", true).apply()
                            showSplash = false
                        }
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }
}
