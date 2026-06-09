package com.app.musicplayer.core.datastore

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class LanguageManagerTest {
    
    private lateinit var context: Context
    private lateinit var resources: Resources
    private lateinit var configuration: Configuration
    private lateinit var languageManager: LanguageManager
    
    @Before
    fun setup() {
        context = mockk()
        resources = mockk()
        configuration = mockk()
        
        every { context.resources } returns resources
        every { resources.configuration } returns configuration
        
        languageManager = LanguageManager(context)
    }
    
    @Test
    fun `test supported languages contains expected entries`() {
        val supportedLanguages = LanguageManager.SUPPORTED_LANGUAGES
        
        assertTrue("Should support system default", 
            supportedLanguages.any { it.code == "" })
        assertTrue("Should support English", 
            supportedLanguages.any { it.code == "en" })
        assertTrue("Should support Simplified Chinese", 
            supportedLanguages.any { it.code == "zh-CN" })
        assertTrue("Should support Traditional Chinese", 
            supportedLanguages.any { it.code == "zh-TW" })
        assertTrue("Should support Spanish", 
            supportedLanguages.any { it.code == "es" })
        assertTrue("Should support French", 
            supportedLanguages.any { it.code == "fr" })
        assertTrue("Should support German", 
            supportedLanguages.any { it.code == "de" })
        assertTrue("Should support Russian", 
            supportedLanguages.any { it.code == "ru" })
    }
    
    @Test
    fun `test isLanguageSupported returns correct values`() {
        assertTrue("English should be supported", 
            languageManager.isLanguageSupported("en"))
        assertTrue("Chinese simplified should be supported", 
            languageManager.isLanguageSupported("zh-CN"))
        assertFalse("Unsupported language should return false", 
            languageManager.isLanguageSupported("xx"))
        assertTrue("System default should be supported", 
            languageManager.isLanguageSupported(""))
    }
    
    @Test
    fun `test getSystemLanguageCode with supported language`() {
        // Mock system locale as Chinese
        val chineseLocale = Locale.forLanguageTag("zh-CN")
        val localeList = LocaleList(chineseLocale)
        
        every { configuration.locales } returns localeList
        
        val result = languageManager.getSystemLanguageCode()
        assertEquals("Should return zh-CN for Chinese system locale", "zh-CN", result)
    }
    
    @Test
    fun `test getSystemLanguageCode with unsupported language falls back to English`() {
        // Mock system locale as unsupported language
        val unsupportedLocale = Locale.forLanguageTag("xx-YY")
        val localeList = LocaleList(unsupportedLocale)
        
        every { configuration.locales } returns localeList
        
        val result = languageManager.getSystemLanguageCode()
        assertEquals("Should fallback to English for unsupported locale", "en", result)
    }
    
    @Test
    fun `test getSystemLanguageCode with base language match`() {
        // Mock system locale as zh-HK (not directly supported but base 'zh' is)
        val hkLocale = Locale.forLanguageTag("zh-HK")
        val localeList = LocaleList(hkLocale)
        
        every { configuration.locales } returns localeList
        
        val result = languageManager.getSystemLanguageCode()
        assertTrue("Should return a Chinese variant for zh-HK", 
            result.startsWith("zh"))
    }
    
    @Test
    fun `test language options have consistent structure`() {
        LanguageManager.SUPPORTED_LANGUAGES.forEach { option ->
            assertNotNull("Display name should not be null", option.displayName)
            assertNotNull("Native name should not be null", option.nativeName)
            assertNotNull("Code should not be null", option.code)
            
            if (option.code.isNotEmpty()) {
                assertTrue("Non-system language codes should contain letters", 
                    option.code.matches(Regex("[a-z-]+")))
            }
        }
    }
}