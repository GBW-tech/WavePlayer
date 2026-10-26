package com.audio.waveplayer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.audio.waveplayer.domain.model.Song
import com.audio.waveplayer.util.formatDuration

@Composable
fun SongDetailsDialog(
    song: Song,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Song Details") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                DetailItem(label = "Title", value = song.title)
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem(label = "Artist", value = song.artist)
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem(label = "Album", value = song.album)
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem(label = "Duration", value = formatDuration(song.duration))
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem(label = "URI", value = song.uri)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
