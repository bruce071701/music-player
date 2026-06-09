package com.app.musicplayer.core.database.mapper

import com.app.musicplayer.core.database.entity.TrackEntity
import com.app.musicplayer.core.model.Track
import com.app.musicplayer.core.model.TrackSource

fun TrackEntity.toDomainModel(): Track {
    return Track(
        id = id,
        source = when (source) {
            "youtube" -> TrackSource.YOUTUBE
            else -> TrackSource.LOCAL
        },
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        durationMs = durationMs,
        trackNumber = trackNumber,
        discNumber = discNumber,
        year = year,
        genre = genre,
        filePath = filePath,
        youtubeId = youtubeId,
        coverUri = coverUri,
        bitrate = bitrate,
        sampleRate = sampleRate,
        fileSize = fileSize,
        replayGainTrack = replayGainTrack,
        replayGainAlbum = replayGainAlbum,
        rating = rating,
        playCount = playCount,
        lastPlayedAt = lastPlayedAt,
        isFavorite = isFavorite,
        addedAt = addedAt
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        source = when (source) {
            TrackSource.YOUTUBE -> "youtube"
            TrackSource.LOCAL -> "local"
        },
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        durationMs = durationMs,
        trackNumber = trackNumber,
        discNumber = discNumber,
        year = year,
        genre = genre,
        filePath = filePath,
        youtubeId = youtubeId,
        coverUri = coverUri,
        bitrate = bitrate,
        sampleRate = sampleRate,
        fileSize = fileSize,
        replayGainTrack = replayGainTrack,
        replayGainAlbum = replayGainAlbum,
        rating = rating,
        playCount = playCount,
        lastPlayedAt = lastPlayedAt,
        isFavorite = isFavorite,
        addedAt = addedAt
    )
}

fun List<TrackEntity>.toDomainModels(): List<Track> = map { it.toDomainModel() }
