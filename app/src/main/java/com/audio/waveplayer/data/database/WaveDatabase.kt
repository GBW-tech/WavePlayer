package com.audio.waveplayer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlaylistEntity::class, FavoriteEntity::class, PlaylistSongEntity::class], version = 2, exportSchema = false)
abstract class WaveDatabase : RoomDatabase() {
    abstract fun waveDao(): WaveDao

    companion object {
        @Volatile
        private var INSTANCE: WaveDatabase? = null

        fun getDatabase(context: Context): WaveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WaveDatabase::class.java,
                    "wave_player_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
