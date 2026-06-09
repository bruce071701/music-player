package com.app.musicplayer.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "eq_presets",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class EqPresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "is_builtin")
    val isBuiltin: Boolean = false,

    @ColumnInfo(name = "bands_json")
    val bandsJson: String,  // JSON array: [0,0,2,3,2,0,-1,-2,0,0]

    @ColumnInfo(name = "preamp")
    val preamp: Float = 0f
)
