package com.audio.waveplayer.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.audio.waveplayer.data.media.MusicRepository
import com.audio.waveplayer.data.media.SongSortOrder
import com.audio.waveplayer.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val musicRepository = MusicRepository(application)

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadSongs(sortOrder: SongSortOrder = SongSortOrder.TITLE_ASC) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loadedSongs = musicRepository.getSongs(sortOrder)
                _songs.value = loadedSongs
            } catch (e: Exception) {
                _songs.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
