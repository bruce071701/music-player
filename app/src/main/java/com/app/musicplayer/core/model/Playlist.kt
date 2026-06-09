package com.app.musicplayer.core.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val type: PlaylistType = PlaylistType.USER,
    val smartRules: SmartRules? = null,
    val coverUri: String? = null,
    val trackCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class PlaylistType {
    USER, SMART, AUTO
}

data class SmartRules(
    val conditions: List<SmartCondition> = emptyList(),
    val limit: Int? = null,
    val orderBy: String? = null
)

data class SmartCondition(
    val field: String,       // "artist", "genre", "year", "rating", "play_count"
    val operator: String,    // "equals", "contains", "greater_than", "less_than"
    val value: String
)
