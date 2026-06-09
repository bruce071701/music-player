package com.app.musicplayer.core.network.musicbrainz

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Inject
import javax.inject.Singleton

interface MusicBrainzApiService {
    @GET("release/{mbid}")
    suspend fun getCoverArt(@Path("mbid") mbid: String): CoverArtResponse
}

data class CoverArtResponse(
    val images: List<CoverArtImage> = emptyList()
)

data class CoverArtImage(
    val image: String = "",
    val thumbnails: CoverArtThumbnails = CoverArtThumbnails(),
    val front: Boolean = false
)

data class CoverArtThumbnails(
    val small: String = "",
    val large: String = "",
    val _250: String = "",
    val _500: String = ""
)

data class CoverArtRequest(
    val mbid: String,
    val onResult: (String?) -> Unit
)

/**
 * Rate-limited MusicBrainz cover art fetcher.
 * Respects 1 request per second limit.
 */
@Singleton
class CoverArtService @Inject constructor(
    private val api: MusicBrainzApiService
) {
    companion object {
        private const val INTERVAL_MS = 1100L // >= 1.1s to be safe
        private const val MAX_RETRIES = 3
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val requestChannel = Channel<CoverArtRequest>(Channel.BUFFERED)

    init {
        scope.launch {
            for (request in requestChannel) {
                val coverUrl = fetchCoverWithRetry(request.mbid)
                request.onResult(coverUrl)
                delay(INTERVAL_MS) // Rate limit
            }
        }
    }

    fun enqueue(mbid: String, onResult: (String?) -> Unit) {
        scope.launch {
            requestChannel.send(CoverArtRequest(mbid, onResult))
        }
    }

    private suspend fun fetchCoverWithRetry(mbid: String): String? {
        repeat(MAX_RETRIES) { attempt ->
            try {
                val response = api.getCoverArt(mbid)
                val frontImage = response.images.find { it.front }
                    ?: response.images.firstOrNull()
                return frontImage?.thumbnails?._500
                    ?: frontImage?.thumbnails?.large
                    ?: frontImage?.image
            } catch (e: HttpException) {
                if (e.code() == 503) {
                    delay(INTERVAL_MS * (attempt + 1)) // Exponential backoff
                } else if (e.code() == 404) {
                    return null // No cover art available
                }
            } catch (e: Exception) {
                delay(INTERVAL_MS)
            }
        }
        return null
    }
}
