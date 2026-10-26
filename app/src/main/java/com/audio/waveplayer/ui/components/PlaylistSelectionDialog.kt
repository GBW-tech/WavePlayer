package com.audio.waveplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.audio.waveplayer.data.database.PlaylistEntity
import com.audio.waveplayer.domain.model.Song

@Composable
fun PlaylistSelectionDialog(
    song: Song,
    playlists: List<PlaylistEntity>,
    onDismiss: () -> Unit,
    onSelectPlaylist: (PlaylistEntity) -> Unit,
    onCreateNewPlaylist: (String) -> Unit
) {
    var showCreateNew by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Playlist") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (showCreateNew) {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text("New Playlist Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (newPlaylistName.isNotBlank()) {
                                onCreateNewPlaylist(newPlaylistName.trim())
                                newPlaylistName = ""
                                showCreateNew = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create & Add")
                    }
                } else {
                    if (playlists.isEmpty()) {
                        Text("No playlists found.")
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            items(playlists) { playlist ->
                                Text(
                                    text = playlist.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectPlaylist(playlist) }
                                        .padding(vertical = 12.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(
                        onClick = { showCreateNew = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Create New Playlist")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
