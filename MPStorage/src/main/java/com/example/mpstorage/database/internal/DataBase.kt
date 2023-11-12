package com.example.mpstorage.database.internal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mpstorage.database.internal.entity.AudioEntity
import com.example.mpstorage.internal.entity.IAudioDao
import kotlin.concurrent.Volatile

@Database(entities = [AudioEntity::class], version = 1)
internal abstract class DataBase: RoomDatabase() {

    abstract fun getAudioDao(): IAudioDao

    companion object{

        @Volatile
        private var INSTANCE: DataBase? = null

        fun create(context: Context): DataBase {
            return INSTANCE ?: synchronized(this){
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