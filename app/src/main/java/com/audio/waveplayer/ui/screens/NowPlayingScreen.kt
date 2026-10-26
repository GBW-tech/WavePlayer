package com.audio.waveplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.audio.waveplayer.data.database.PlaylistEntity
import com.audio.waveplayer.domain.model.Song
import com.audio.waveplayer.ui.components.AudioVisualizer
import com.audio.waveplayer.ui.components.PlaylistSelectionDialog
import com.audio.waveplayer.ui.components.SongDetailsDialog
import com.audio.waveplayer.util.formatDuration
import kotlin.math.roundToInt

@Composable
fun NowPlayingScreen(
    currentSong: Song?,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    isFavorite: Boolean,
    playlists: List<PlaylistEntity>,
    transitionProgress: Float, // 0f (mini) to 1f (full)
    onPlayPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToPlaylist: (PlaylistEntity) -> Unit,
    onCreatePlaylistAndAdd: (String) -> Unit,
    onOpenEqualizer: () -> Unit,
    onBack: () -> Unit,
    onDragProgress: (Float) -> Unit = {},
    onDragEnd: (Boolean) -> Unit = {}
) {
    var totalDrag by remember { mutableFloatStateOf(0f) }

    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showSongDetailsDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    if (showPlaylistDialog && currentSong != null) {
        PlaylistSelectionDialog(
            song = currentSong,
            playlists = playlists,
            onDismiss = { showPlaylistDialog = false },
            onSelectPlaylist = { playlist ->
                onAddToPlaylist(playlist)
                showPlaylistDialog = false
            },
            onCreateNewPlaylist = { name ->
                onCreatePlaylistAndAdd(name)
                showPlaylistDialog = false
            }
        )
    }

    if (showSongDetailsDialog && currentSong != null) {
        SongDetailsDialog(
            song = currentSong,
            onDismiss = { showSongDetailsDialog = false }
        )
    }

    val artworkSize = 40.dp + (200.dp * transitionProgress)
    val cornerRadius = 8.dp + (16.dp * transitionProgress)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .offset { IntOffset(0, totalDrag.roundToInt().coerceAtLeast(0)) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                        val fraction = (-totalDrag / size.height).coerceIn(-1f, 0f)
                        onDragProgress(fraction)
                    },
                    onDragEnd = {
                        val shouldDismiss = totalDrag > 120f
                        totalDrag = 0f
                        onDragEnd(shouldDismiss)
                        if (shouldDismiss) {
                            onBack()
                        }
                    }
                )
            }
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        if (transitionProgress > 0.4f) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = { showPlaylistDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                            contentDescription = "Add to Playlist",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Equalizer") },
                                onClick = {
                                    showMoreMenu = false
                                    onOpenEqualizer()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Song Details") },
                                onClick = {
                                    showMoreMenu = false
                                    showSongDetailsDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites") },
                                onClick = {
                                    showMoreMenu = false
                                    onFavoriteClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Add to Playlist") },
                                onClick = {
                                    showMoreMenu = false
                                    showPlaylistDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Album Artwork shrinking & travelling
        Box(
            modifier = Modifier
                .size(artworkSize)
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(artworkSize * 0.4f),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Metadata
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = currentSong?.title ?: "No track playing",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentSong?.artist ?: "Unknown Artist",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Progress Slider
        val sliderPosition = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
        Slider(
            value = sliderPosition.coerceIn(0f, 1f),
            onValueChange = { newVal ->
                onSeek((newVal * duration).toLong())
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Shuffle */ }) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { /* Previous */ }) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            // Play/Pause main button
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onPlayPauseClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = { /* Next */ }) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(onClick = { /* Repeat */ }) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subtle Audio Visualizer in the lower portion
        AudioVisualizer(
            isPlaying = isPlaying,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
