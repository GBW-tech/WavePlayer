package com.audio.waveplayer.data.media

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.audio.waveplayer.domain.model.Album
import com.audio.waveplayer.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository(private val context: Context) {

    suspend fun getSongs(sortOrder: SongSortOrder = SongSortOrder.TITLE_ASC): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sort = when (sortOrder) {
            SongSortOrder.TITLE_ASC -> "${MediaStore.Audio.Media.TITLE} ASC"
            SongSortOrder.TITLE_DESC -> "${MediaStore.Audio.Media.TITLE} DESC"
            SongSortOrder.ARTIST_ASC -> "${MediaStore.Audio.Media.ARTIST} ASC"
            SongSortOrder.DATE_ADDED_DESC -> "${MediaStore.Audio.Media.DATE_ADDED} DESC"
            SongSortOrder.DURATION_DESC -> "${MediaStore.Audio.Media.DURATION} DESC"
        }

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sort
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn) ?: "Unknown Title"
                val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                val album = cursor.getString(albumColumn) ?: "Unknown Album"
                val duration = cursor.getLong(durationColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)

                val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val artworkUri = ContentUris.withAppendedId(
                    android.net.Uri.parse("content://media/external/audio/albumart"),
                    albumId
                ).toString()

                songs.add(
                    Song(
                        id = id,
                        title = title,
                        artist = artist,
                        album = album,
                        duration = duration,
                        uri = contentUri.toString(),
                        artworkUri = artworkUri,
                        dateAdded = dateAdded
                    )
                )
            }
        }
        songs
    }

    suspend fun getAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val albums = mutableListOf<Album>()
        val collection = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        )

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            "${MediaStore.Audio.Albums.ALBUM} ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val numSongsCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(albumCol) ?: "Unknown Album"
                val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                val numSongs = cursor.getInt(numSongsCol)
                val artUri = ContentUris.withAppendedId(
                    android.net.Uri.parse("content://media/external/audio/albumart"),
                    id
                ).toString()

                albums.add(Album(id, title, artist, artUri, numSongs))
            }
        }
        albums
    }
}

enum class SongSortOrder {
    TITLE_ASC, TITLE_DESC, ARTIST_ASC, DATE_ADDED_DESC, DURATION_DESC
}
