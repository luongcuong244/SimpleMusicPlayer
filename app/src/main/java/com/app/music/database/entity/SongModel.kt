package com.app.music.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class SongModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "artist")
    val artist: String,
    @ColumnInfo(name = "thumbnail_link")
    val thumbnailLink: String,
    @ColumnInfo(name = "music_link")
    val musicLink: String,
    @ColumnInfo(name = "is_favourite")
    var isFavourite: Boolean = false
) : Serializable
