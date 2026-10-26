package com.audio.waveplayer.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Equalizer
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
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.audio.waveplayer.data.database.PlaylistEntity
import com.audio.waveplayer.data.preferences.ThemeDesignStyle
import com.audio.waveplayer.domain.model.Song
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
    designStyle: ThemeDesignStyle,
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

    val artworkSize = 64.dp + (220.dp * transitionProgress)
    val cornerRadius = 12.dp + (20.dp * transitionProgress)

    val isGlass = designStyle == ThemeDesignStyle.LIQUID_GLASS

    // Dynamic background gradient
    val bgColors = if (isGlass) {
        listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            Color.Black.copy(alpha = 0.9f)
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.surfaceContainer,
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgColors))
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
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
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

                    IconButton(onClick = { /* Queue */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = "Queue",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Central Large Artwork
            Box(
                modifier = Modifier
                    .size(artworkSize)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(
                        if (isGlass) {
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(artworkSize * 0.45f),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Song Info & Actions (Title, Artist, Like, Download, Playlist)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Like Button
                        GlassActionButton(
                            isGlass = isGlass,
                            onClick = onFavoriteClick
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Add to Playlist Button
                        GlassActionButton(
                            isGlass = isGlass,
                            onClick = { showPlaylistDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                contentDescription = "Add to Playlist",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Download Button
                        GlassActionButton(
                            isGlass = isGlass,
                            onClick = { /* Download */ }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Playback Timeline (Seek Bar) & Timestamps (Image 2)
            Column(modifier = Modifier.fillMaxWidth()) {
                val sliderPosition = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
                
                if (isGlass) {
                    LiquidGlassSeekBar(
                        progress = sliderPosition,
                        isGlass = true,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        onSeek = { newVal ->
                            onSeek((newVal * duration).toLong())
                        }
                    )
                } else {
                    Slider(
                        value = sliderPosition.coerceIn(0f, 1f),
                        onValueChange = { newVal ->
                            onSeek((newVal * duration).toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
            }

            // Main Playback Controls (Previous, Translucent Circular Glass Play/Pause, Next) (Image 3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassControlButton(isGlass = isGlass, size = 52.dp, onClick = { /* Previous */ }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Translucent Circular Glass Play/Pause Button (Image 3)
                GlassControlButton(
                    isGlass = isGlass,
                    size = 80.dp,
                    isPrimary = true,
                    onClick = onPlayPauseClick
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(44.dp),
                        tint = if (isGlass) Color.White else MaterialTheme.colorScheme.onPrimary
                    )
                }

                GlassControlButton(isGlass = isGlass, size = 52.dp, onClick = { /* Next */ }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Secondary Playboard Controls Row (Styled identically as compact glass action buttons)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassActionButton(isGlass = isGlass, onClick = { /* Shuffle */ }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                GlassActionButton(isGlass = isGlass, onClick = { /* Repeat */ }) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                GlassActionButton(isGlass = isGlass, onClick = { /* Sleep Timer */ }) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Sleep Timer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                GlassActionButton(isGlass = isGlass, onClick = onOpenEqualizer) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = "Equalizer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box {
                    GlassActionButton(isGlass = isGlass, onClick = { showMoreMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Song Details") },
                            onClick = {
                                showMoreMenu = false
                                showSongDetailsDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Equalizer") },
                            onClick = {
                                showMoreMenu = false
                                onOpenEqualizer()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites") },
                            onClick = {
                                showMoreMenu = false
                                onFavoriteClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiquidGlassSeekBar(
    progress: Float,
    isGlass: Boolean,
    primaryColor: Color,
    onSeek: (Float) -> Unit
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val maxWidth = constraints.maxWidth.toFloat()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            if (maxWidth > 0f) {
                                val p = (offset.x / maxWidth).coerceIn(0f, 1f)
                                onSeek(p)
                            }
                        },
                        onHorizontalDrag = { change, _ ->
                            if (maxWidth > 0f) {
                                val p = (change.position.x / maxWidth).coerceIn(0f, 1f)
                                onSeek(p)
                            }
                        }
                    )
                }
                .clickable {
                    onSeek(0.5f)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Unplayed track (translucent glass channel)
                drawRect(
                    color = Color.White.copy(alpha = 0.22f),
                    size = Size(canvasWidth, canvasHeight)
                )

                // Played track (luminous gradient glass)
                val playedWidth = canvasWidth * progress.coerceIn(0f, 1f)
                if (playedWidth > 0f) {
                    val gradient = Brush.horizontalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.85f)
                        ),
                        startX = 0f,
                        endX = playedWidth
                    )
                    drawRect(brush = gradient, size = Size(playedWidth, canvasHeight))
                    // Edge highlight for glass thickness
                    drawRect(
                        color = Color.White.copy(alpha = 0.5f),
                        size = Size(playedWidth, canvasHeight * 0.35f)
                    )
                }
            }

            // Glass Playhead Thumb
            if (maxWidth > 0f) {
                val thumbOffset = with(density) { (maxWidth * progress.coerceIn(0f, 1f)).toDp() - 8.dp }
                Box(
                    modifier = Modifier
                        .offset(x = thumbOffset)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color.White, Color.White.copy(alpha = 0.7f))))
                        .border(1.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                )
            }
        }
    }
}

@Composable
fun GlassActionButton(
    isGlass: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                if (isGlass) Color.White.copy(alpha = 0.16f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (isGlass) 1.dp else 0.dp,
                color = if (isGlass) Color.White.copy(alpha = 0.35f) else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun GlassControlButton(
    isGlass: Boolean,
    size: androidx.compose.ui.unit.Dp,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val bgBrush = if (isPrimary) {
        if (isGlass) {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.12f)
                )
            )
        } else {
            Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.tertiary
                )
            )
        }
    } else {
        if (isGlass) {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.22f),
                    Color.White.copy(alpha = 0.08f)
                )
            )
        } else {
            Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    }

    val borderColor = if (isGlass) Color.White.copy(alpha = 0.45f) else Color.Transparent

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bgBrush)
            .border(
                width = if (isGlass) 1.dp else 0.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
