package com.example.mpstorage.database

import android.content.Context
import com.example.mpstorage.database.internal.IAudioDataProvider
import com.example.mpstorage.database.internal.factory.AudioDataProviderFactory

object DataBaseApi {

    fun forAudio(context: Context): IAudioDataProvider{
        return AudioDataProviderFactory.create(context)
    }
}