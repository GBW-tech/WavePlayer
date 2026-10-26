package com.audio.waveplayer.domain.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    val artworkUri: String?,
    val dateAdded: Long,
    val isFavorite: Boolean = false
)
