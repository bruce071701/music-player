package com.app.musicplayer.core.network.lastfm

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LastFmApiService {

    @FormUrlEncoded
    @POST("2.0/")
    suspend fun scrobble(
        @Field("method") method: String = "track.scrobble",
        @Field("artist") artist: String,
        @Field("track") track: String,
        @Field("album") album: String? = null,
        @Field("timestamp") timestamp: Long,
        @Field("api_key") apiKey: String,
        @Field("sk") sessionKey: String,
        @Field("api_sig") signature: String,
        @Field("format") format: String = "json"
    ): LastFmResponse

    @FormUrlEncoded
    @POST("2.0/")
    suspend fun updateNowPlaying(
        @Field("method") method: String = "track.updateNowPlaying",
        @Field("artist") artist: String,
        @Field("track") track: String,
        @Field("album") album: String? = null,
        @Field("api_key") apiKey: String,
        @Field("sk") sessionKey: String,
        @Field("api_sig") signature: String,
        @Field("format") format: String = "json"
    ): LastFmResponse

    @GET("2.0/")
    suspend fun getSession(
        @Query("method") method: String = "auth.getSession",
        @Query("token") token: String,
        @Query("api_key") apiKey: String,
        @Query("api_sig") signature: String,
        @Query("format") format: String = "json"
    ): LastFmSessionResponse
}

// Response models
data class LastFmResponse(
    val scrobbles: ScrobbleResult? = null,
    val error: Int? = null,
    val message: String? = null
)

data class ScrobbleResult(
    val accepted: Int = 0,
    val ignored: Int = 0
)

data class LastFmSessionResponse(
    val session: LastFmSession? = null,
    val error: Int? = null,
    val message: String? = null
)

data class LastFmSession(
    val name: String = "",
    val key: String = "",
    val subscriber: Int = 0
)
