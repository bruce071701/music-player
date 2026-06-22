package com.app.musicplayer.core.datastore

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

data class LanguageOption(
    val code: String,  // "" for system default
    val displayName: String,
    val nativeName: String
)

@Singleton
class LanguageManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")

        val SUPPORTED_LANGUAGES = listOf(
            LanguageOption("", "System Default", "System"),
            LanguageOption("en", "English", "English"),
            LanguageOption("zh-CN", "Chinese (Simplified)", "简体中文"),
            LanguageOption("zh-TW", "Chinese (Traditional)", "繁體中文"),
            LanguageOption("es", "Spanish", "Español"),
            LanguageOption("pt", "Portuguese", "Português"),
            LanguageOption("ja", "Japanese", "日本語"),
            LanguageOption("ko", "Korean", "한국어"),
            LanguageOption("in", "Indonesian", "Bahasa Indonesia"),
            LanguageOption("fr", "French", "Français"),
            LanguageOption("de", "German", "Deutsch"),
            LanguageOption("ru", "Russian", "Русский")
        )
    }

    val currentLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: ""
    }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = languageCode
        }
        applyLanguage(languageCode)
    }

    fun applyLanguage(languageCode: String) {
        try {
            val localeList = if (languageCode.isBlank()) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(languageCode)
            }
            AppCompatDelegate.setApplicationLocales(localeList)
        } catch (e: Exception) {
            // Fallback to system default if language application fails
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        }
    }

    /**
     * Restore the saved language preference on app cold start.
     * On Android 13+ (API 33), AppCompatDelegate auto-persists via system LocaleManager,
     * but on older versions we need to read from DataStore and re-apply.
     */
    fun restoreSavedLanguage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ handles persistence natively via per-app language settings
            return
        }
        // For older versions, read from DataStore and apply the locale
        // before any Activity renders
        try {
            val savedCode = runBlocking {
                context.dataStore.data.first()[LANGUAGE_KEY] ?: ""
            }
            if (savedCode.isNotBlank()) {
                applyLanguage(savedCode)
            }
        } catch (_: Exception) {
            // If DataStore can't be read yet, ignore - language will be system default
        }
    }

    fun getCurrentDisplayName(): String {
        return try {
            val currentLocales = AppCompatDelegate.getApplicationLocales()
            if (currentLocales.isEmpty) return "System"
            val tag = currentLocales.toLanguageTags()
            SUPPORTED_LANGUAGES.find { it.code == tag }?.nativeName ?: tag
        } catch (e: Exception) {
            "System"
        }
    }

    fun isLanguageSupported(languageCode: String): Boolean {
        return SUPPORTED_LANGUAGES.any { it.code == languageCode }
    }

    fun getSystemLanguageCode(): String {
        return try {
            val systemLocale = context.resources.configuration.locales[0]
            val languageTag = systemLocale.toLanguageTag()
            
            // Check if we support this exact language
            if (SUPPORTED_LANGUAGES.any { it.code == languageTag }) {
                return languageTag
            }
            
            // Check if we support the base language (e.g., "zh" for "zh-HK")
            val baseLanguage = systemLocale.language
            SUPPORTED_LANGUAGES.find { it.code.startsWith(baseLanguage) }?.code ?: "en"
        } catch (e: Exception) {
            "en" // Default to English
        }
    }
}
