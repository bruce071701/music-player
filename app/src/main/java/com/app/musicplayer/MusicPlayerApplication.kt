package com.app.musicplayer

import android.app.Application
import com.app.musicplayer.core.datastore.LanguageManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MusicPlayerApplication : Application() {

    @Inject
    lateinit var languageManager: LanguageManager

    override fun onCreate() {
        super.onCreate()
        // Restore saved language on app cold start (needed for API < 33)
        languageManager.restoreSavedLanguage()
    }
}
