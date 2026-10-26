package com.audio.waveplayer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.audio.waveplayer.data.preferences.ThemeDesignStyle
import com.audio.waveplayer.data.preferences.ThemeMode
import com.audio.waveplayer.data.preferences.ThemePreferencesManager
import com.audio.waveplayer.data.preferences.ThemeSettings
import com.audio.waveplayer.playback.PlaybackViewModel
import com.audio.waveplayer.ui.components.LiquidGlassNavBar
import com.audio.waveplayer.ui.components.MiniPlayer
import com.audio.waveplayer.ui.screens.LibraryScreen
import com.audio.waveplayer.ui.screens.NowPlayingScreen
import com.audio.waveplayer.ui.screens.SoundLabScreen
import com.audio.waveplayer.ui.theme.GlassCard
import com.audio.waveplayer.ui.theme.WavePlayerTheme
import com.audio.waveplayer.util.PlaylistBackupHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val themePreferencesManager = ThemePreferencesManager(applicationContext)
        val backupHelper = PlaylistBackupHelper(applicationContext)

        setContent {
            val themeSettings by themePreferencesManager.themeSettingsFlow.collectAsState(
                initial = ThemeSettings()
            )
            val coroutineScope = rememberCoroutineScope()
            val playbackViewModel: PlaybackViewModel = viewModel()
            val currentSong by playbackViewModel.currentSong.collectAsState()
            val isPlaying by playbackViewModel.isPlaying.collectAsState()
            val currentPosition by playbackViewModel.currentPosition.collectAsState()
            val duration by playbackViewModel.duration.collectAsState()
            val favoriteIds by playbackViewModel.favoriteSongIds.collectAsState()
            val playlists by playbackViewModel.playlists.collectAsState()

            val context = LocalContext.current

            val exportLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("application/json")
            ) { uri ->
                uri?.let {
                    coroutineScope.launch {
                        try {
                            val jsonString = backupHelper.exportPlaylistsToJson()
                            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                outputStream.write(jsonString.toByteArray())
                            }
                            Toast.makeText(context, "Playlists exported successfully!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Export failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            val importLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri ->
                uri?.let {
                    coroutineScope.launch {
                        try {
                            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                inputStream.bufferedReader().use { it.readText() }
                            } ?: ""
                            val result = backupHelper.importPlaylistsFromJson(jsonString)
                            result.fold(
                                onSuccess = { count ->
                                    Toast.makeText(context, "Successfully imported $count playlists!", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = { e ->
                                    Toast.makeText(context, "Import failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } catch (e: Exception) {
                            Toast.makeText(context, "Import failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            WavePlayerTheme(
                themeMode = themeSettings.themeMode,
                useDynamicColor = themeSettings.useDynamicColor,
                customAccentColor = themeSettings.customAccentColor
            ) {
                var selectedTab by remember { mutableIntStateOf(0) }
                var currentScreen by remember { mutableStateOf("main") }

                val transitionProgress = remember { Animatable(0f) }

                LaunchedEffect(currentScreen) {
                    if (currentScreen == "now_playing") {
                        transitionProgress.animateTo(1f, spring(stiffness = 300f, dampingRatio = 0.8f))
                    } else {
                        transitionProgress.animateTo(0f, spring(stiffness = 300f, dampingRatio = 0.8f))
                    }
                }

                BackHandler(enabled = currentScreen != "main") {
                    currentScreen = "main"
                }

                when (currentScreen) {
                    "soundlab" -> {
                        SoundLabScreen(onBack = { currentScreen = "main" })
                    }
                    else -> {
                        Scaffold(
                            topBar = {
                                if (currentScreen != "now_playing") {
                                    TopAppBar(
                                        title = { Text(if (selectedTab == 0) "WavePlayer Library" else "Settings") }
                                    )
                                }
                            },
                            bottomBar = {
                                if (currentScreen != "now_playing") {
                                    Column {
                                        MiniPlayer(
                                            currentSong = currentSong,
                                            isPlaying = isPlaying,
                                            onPlayPauseClick = { playbackViewModel.togglePlayPause() },
                                            onMiniPlayerClick = { currentScreen = "now_playing" }
                                        )
                                        if (themeSettings.designStyle == ThemeDesignStyle.LIQUID_GLASS) {
                                            LiquidGlassNavBar(
                                                selectedTab = selectedTab,
                                                onTabSelected = { selectedTab = it }
                                            )
                                        } else {
                                            NavigationBar {
                                                NavigationBarItem(
                                                    selected = selectedTab == 0,
                                                    onClick = { selectedTab = 0 },
                                                    icon = { Icon(Icons.Default.MusicNote, contentDescription = "Library") },
                                                    label = { Text("Library") }
                                                )
                                                NavigationBarItem(
                                                    selected = selectedTab == 1,
                                                    onClick = { selectedTab = 1 },
                                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                                    label = { Text("Settings") }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        ) { padding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(padding)
                            ) {
                                // Underlying main content (Library or Settings) with Android 16/17 background reveal scale
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            val prog = transitionProgress.value
                                            val scale = 0.96f + (0.04f * prog)
                                            scaleX = scale
                                            scaleY = scale
                                            alpha = 1f - (0.15f * (1f - prog))
                                        }
                                ) {
                                    if (selectedTab == 0) {
                                        LibraryScreen(onSongClick = { song ->
                                            playbackViewModel.playSong(song)
                                        })
                                    } else {
                                        SettingsScreen(
                                            themeSettings = themeSettings,
                                            onThemeModeChanged = { mode ->
                                                if (mode == ThemeMode.AMOLED && themeSettings.useDynamicColor) {
                                                    // Handled via toast in settings UI
                                                } else {
                                                    coroutineScope.launch { themePreferencesManager.setThemeMode(mode) }
                                                }
                                            },
                                            onDynamicColorChanged = { enabled ->
                                                coroutineScope.launch { themePreferencesManager.setUseDynamicColor(enabled) }
                                            },
                                            onDesignStyleChanged = { style ->
                                                coroutineScope.launch { themePreferencesManager.setDesignStyle(style) }
                                            },
                                            onSoundLabClick = { currentScreen = "soundlab" },
                                            onExportClick = { exportLauncher.launch("waveplayer_playlists_backup.json") },
                                            onImportClick = { importLauncher.launch(arrayOf("application/json", "*/*")) }
                                        )
                                    }
                                }

                                // Now Playing overlay expanding smoothly upward from mini-player position
                                if (currentScreen == "now_playing" || transitionProgress.value > 0.001f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.background)
                                            .graphicsLayer {
                                                val p = transitionProgress.value
                                                transformOrigin = TransformOrigin(0.5f, 1f)
                                                scaleX = 0.15f + (0.85f * p)
                                                scaleY = 0.15f + (0.85f * p)
                                                alpha = p
                                            }
                                    ) {
                                        val isFav = currentSong?.let { favoriteIds.contains(it.id) } ?: false
                                        NowPlayingScreen(
                                            currentSong = currentSong,
                                            isPlaying = isPlaying,
                                            currentPosition = currentPosition,
                                            duration = duration,
                                            isFavorite = isFav,
                                            playlists = playlists,
                                            transitionProgress = transitionProgress.value,
                                            onPlayPauseClick = { playbackViewModel.togglePlayPause() },
                                            onSeek = { playbackViewModel.seekTo(it) },
                                            onFavoriteClick = { currentSong?.let { playbackViewModel.toggleFavorite(it) } },
                                            onAddToPlaylist = { playlist -> currentSong?.let { playbackViewModel.addToPlaylist(playlist, it) } },
                                            onCreatePlaylistAndAdd = { name -> currentSong?.let { playbackViewModel.createPlaylistAndAdd(name, it) } },
                                            onOpenEqualizer = { currentScreen = "soundlab" },
                                            onBack = { currentScreen = "main" },
                                            onDragProgress = { fraction ->
                                                coroutineScope.launch {
                                                    val newProg = (1f + fraction).coerceIn(0f, 1f)
                                                    transitionProgress.snapTo(newProg)
                                                }
                                            },
                                            onDragEnd = { shouldDismiss ->
                                                coroutineScope.launch {
                                                    if (shouldDismiss) {
                                                        transitionProgress.animateTo(0f, spring(stiffness = 400f))
                                                        currentScreen = "main"
                                                    } else {
                                                        transitionProgress.animateTo(1f, spring(stiffness = 300f))
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    themeSettings: ThemeSettings,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    onDesignStyleChanged: (ThemeDesignStyle) -> Unit,
    onSoundLabClick: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Design System Architecture
        SettingsSectionHeader(icon = Icons.Default.Palette, title = "Design System")

        val designCardContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Visual Theme Architecture",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose between native Material Expressive and translucent Liquid Glass design systems.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isExpressive = themeSettings.designStyle == ThemeDesignStyle.MATERIAL_EXPRESSIVE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isExpressive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onDesignStyleChanged(ThemeDesignStyle.MATERIAL_EXPRESSIVE) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Material Expressive",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isExpressive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val isGlass = themeSettings.designStyle == ThemeDesignStyle.LIQUID_GLASS
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isGlass) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onDesignStyleChanged(ThemeDesignStyle.LIQUID_GLASS) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Liquid Glass",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isGlass) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (themeSettings.designStyle == ThemeDesignStyle.LIQUID_GLASS) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                designCardContent()
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(16.dp)
            ) {
                designCardContent()
            }
        }

        // Section: Appearance
        SettingsSectionHeader(icon = Icons.Default.Palette, title = "Appearance")

        val appearanceCardContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dynamic Color Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Material You Dynamic Color",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Derive colors from device wallpaper",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = themeSettings.useDynamicColor,
                        onCheckedChange = onDynamicColorChanged
                    )
                }

                // Theme Mode Selection
                Text(
                    text = "Theme Mode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        val isSelected = themeSettings.themeMode == mode
                        val isAmoledDisabled = mode == ThemeMode.AMOLED && themeSettings.useDynamicColor

                        val containerColor = when {
                            isAmoledDisabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            isSelected -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .clip(RoundedCornerShape(12.dp))
                                .background(containerColor)
                                .clickable {
                                    if (isAmoledDisabled) {
                                        Toast.makeText(
                                            context,
                                            "Material You Dynamic Colour is enabled. Turn it off to use AMOLED mode.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        onThemeModeChanged(mode)
                                    }
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.name,
                                style = MaterialTheme.typography.labelLarge,
                                color = when {
                                    isAmoledDisabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }
        }

        if (themeSettings.designStyle == ThemeDesignStyle.LIQUID_GLASS) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                appearanceCardContent()
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(16.dp)
            ) {
                appearanceCardContent()
            }
        }

        // Section: Playback & Audio
        SettingsSectionHeader(icon = Icons.Default.VolumeUp, title = "Playback & Audio")
        val playbackCardContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Sound Lab & Equalizer",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Configure audio effects, bass boost and presets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (themeSettings.designStyle == ThemeDesignStyle.LIQUID_GLASS) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSoundLabClick() }
            ) {
                playbackCardContent()
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSoundLabClick() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(16.dp)
            ) {
                playbackCardContent()
            }
        }

        // Section: Library & Data
        SettingsSectionHeader(icon = Icons.Default.Storage, title = "Library & Data")
        val libraryCardContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Playlist Backup & Restore",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Export and import your playlists locally via JSON",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onExportClick) {
                        Text("Export JSON")
                    }
                    OutlinedButton(onClick = onImportClick) {
                        Text("Import JSON")
                    }
                }
            }
        }

        if (themeSettings.designStyle == ThemeDesignStyle.LIQUID_GLASS) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                libraryCardContent()
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(16.dp)
            ) {
                libraryCardContent()
            }
        }

        // Section: About
        SettingsSectionHeader(icon = Icons.Default.Info, title = "About")
        val aboutCardContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "WavePlayer",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Version 1.0 (Premium Offline Edition)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Built with Jetpack Compose & Material 3 Expressive",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        if (themeSettings.designStyle == ThemeDesignStyle.LIQUID_GLASS) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                aboutCardContent()
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(16.dp)
            ) {
                aboutCardContent()
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingsSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
