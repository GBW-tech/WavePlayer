package com.audio.waveplayer.playback

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.audio.waveplayer.data.database.FavoriteEntity
import com.audio.waveplayer.data.database.PlaylistEntity
import com.audio.waveplayer.data.database.PlaylistSongEntity
import com.audio.waveplayer.data.database.WaveDatabase
import com.audio.waveplayer.domain.model.Song
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaybackViewModel(application: Application) : AndroidViewModel(application) {
    private val waveDao = WaveDatabase.getDatabase(application).waveDao()

    val favoriteSongIds: StateFlow<List<Long>> = waveDao.getFavoriteSongIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<PlaylistEntity>> = waveDao.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private var mediaController: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    init {
        val sessionToken = SessionToken(application, ComponentName(application, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        controllerFuture.addListener({
            mediaController = controllerFuture.get().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }
                })
            }
        }, MoreExecutors.directExecutor())

        viewModelScope.launch {
            while (true) {
                mediaController?.let { controller ->
                    if (controller.isPlaying) {
                        _currentPosition.value = controller.currentPosition
                        _duration.value = controller.duration.coerceAtLeast(0L)
                    }
                }
                delay(500L)
            }
        }
    }

    fun playSong(song: Song) {
        mediaController?.let { controller ->
            _currentSong.value = song
            val mediaItem = MediaItem.Builder()
                .setUri(song.uri)
                .setMediaId(song.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .build()
                )
                .build()

            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            val currentFavorites = favoriteSongIds.value
            if (currentFavorites.contains(song.id)) {
                waveDao.removeFavorite(song.id)
            } else {
                waveDao.addFavorite(FavoriteEntity(song.id))
            }
        }
    }

    fun addToPlaylist(playlist: PlaylistEntity, song: Song) {
        viewModelScope.launch {
            waveDao.insertPlaylistSong(
                PlaylistSongEntity(
                    playlistId = playlist.id,
                    songUri = song.uri,
                    title = song.title,
                    artist = song.artist,
                    duration = song.duration
                )
            )
        }
    }

    fun createPlaylistAndAdd(name: String, song: Song) {
        viewModelScope.launch {
            val newId = waveDao.insertPlaylist(PlaylistEntity(name = name))
            waveDao.insertPlaylistSong(
                PlaylistSongEntity(
                    playlistId = newId,
                    songUri = song.uri,
                    title = song.title,
                    artist = song.artist,
                    duration = song.duration
                )
            )
        }
    }

    fun togglePlayPause() {
        mediaController?.let { controller ->
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }
        }
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        MediaController.releaseFuture(controllerFuture)
    }
}
