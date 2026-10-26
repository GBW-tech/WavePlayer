package com.audio.waveplayer.util

import android.content.Context
import com.audio.waveplayer.data.database.PlaylistEntity
import com.audio.waveplayer.data.database.WaveDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PlaylistBackupHelper(private val context: Context) {
    private val dao = WaveDatabase.getDatabase(context).waveDao()

    suspend fun exportPlaylistsToJson(): String = withContext(Dispatchers.IO) {
        val playlists = dao.getAllPlaylistsSync()
        val json = JSONObject()
        json.put("version", 1)
        json.put("exportedAt", System.currentTimeMillis())

        val array = JSONArray()
        for (p in playlists) {
            val obj = JSONObject()
            obj.put("name", p.name)
            obj.put("createdAt", p.createdAt)
            array.put(obj)
        }
        json.put("playlists", array)
        json.toString()
    }

    suspend fun importPlaylistsFromJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(jsonString)
            val version = json.optInt("version", 1)
            if (version > 1) {
                return@withContext Result.failure(Exception("Unsupported backup version"))
            }

            val playlistsArray = json.optJSONArray("playlists") ?: JSONArray()
            var importedCount = 0

            for (i in 0 until playlistsArray.length()) {
                val obj = playlistsArray.getJSONObject(i)
                val name = obj.optString("name", "")
                val createdAt = obj.optLong("createdAt", System.currentTimeMillis())

                if (name.isNotBlank()) {
                    dao.insertPlaylist(PlaylistEntity(name = name, createdAt = createdAt))
                    importedCount++
                }
            }
            Result.success(importedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
