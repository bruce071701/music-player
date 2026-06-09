package com.app.musicplayer.core.network.lastfm

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.penji.musicplayer.offline.BuildConfig
import com.app.musicplayer.core.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastFmAuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val lastFmApi: LastFmApiService
) {
    companion object {
        private val SESSION_KEY = stringPreferencesKey("lastfm_session_key")
        private val USERNAME = stringPreferencesKey("lastfm_username")
        private const val AUTH_URL = "https://www.last.fm/api/auth/"
    }

    val sessionKey: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SESSION_KEY]
    }

    val username: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USERNAME]
    }

    val isConnected: Flow<Boolean> = sessionKey.map { it != null }

    /**
     * Opens browser for Last.fm Web Authentication.
     */
    fun startAuth() {
        val url = "${AUTH_URL}?api_key=${BuildConfig.LASTFM_API_KEY}&cb=musicplayer://lastfm/auth"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Complete auth flow with token received from callback.
     */
    suspend fun completeAuth(token: String): Boolean {
        return try {
            val sig = generateSignature(
                mapOf(
                    "api_key" to BuildConfig.LASTFM_API_KEY,
                    "method" to "auth.getSession",
                    "token" to token
                )
            )

            val response = lastFmApi.getSession(
                token = token,
                apiKey = BuildConfig.LASTFM_API_KEY,
                signature = sig
            )

            response.session?.let { session ->
                context.dataStore.edit { prefs ->
                    prefs[SESSION_KEY] = session.key
                    prefs[USERNAME] = session.name
                }
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Disconnect Last.fm account.
     */
    suspend fun disconnect() {
        context.dataStore.edit { prefs ->
            prefs.remove(SESSION_KEY)
            prefs.remove(USERNAME)
        }
    }

    /**
     * Generate API signature for Last.fm authentication.
     * sig = md5(sorted_params + secret)
     */
    fun generateSignature(params: Map<String, String>): String {
        val sortedParams = params.toSortedMap()
        val sigString = sortedParams.entries.joinToString("") { "${it.key}${it.value}" }
        val withSecret = sigString + BuildConfig.LASTFM_API_SECRET

        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(withSecret.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
