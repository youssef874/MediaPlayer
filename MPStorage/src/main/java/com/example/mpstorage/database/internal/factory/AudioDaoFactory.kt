package com.example.mpstorage.database.internal.factory

import android.content.Context
import com.example.mpstorage.database.internal.DataBase
import com.example.mpstorage.database.internal.IAudioDao

internal object AudioDaoFactory: IAudioDaoFactory {

    override fun create(context: Context): IAudioDao {
        return DataBase.create(context).getAudioDao()
    }

}