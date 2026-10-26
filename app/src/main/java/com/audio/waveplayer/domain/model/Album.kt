package com.audio.waveplayer.domain.model

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val artUri: String?,
    val songCount: Int
)
