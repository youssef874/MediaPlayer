package com.example.mpstorage.database.internal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mpstorage.database.internal.entity.AudioEntity
import com.example.mpstorage.database.internal.entity.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import kotlin.concurrent.Volatile

@Database(
    entities = [AudioEntity::class, PlayListEntity::class, PlaylistSongCrossRef::class],
    version = 1,
    exportSchema = true
)
internal abstract class DataBase : RoomDatabase() {

    abstract fun getAudioDao(): IAudioDao

    abstract fun getPlayListDao(): IPlayListDao

    companion object {

        @Volatile
        private var INSTANCE: DataBase? = null

        fun create(context: Context): DataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    DataBase::class.java,
                    "mp_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}