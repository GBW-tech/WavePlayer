package com.audio.waveplayer.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_songs")
data class PlaylistSongEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: Long,
    val songUri: String,
    val title: String,
    val artist: String,
    val duration: Long
)
