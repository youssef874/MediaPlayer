package com.example.mpstorage.database

import android.content.Context
import com.example.mpstorage.database.internal.IAudioDataProvider
import com.example.mpstorage.database.internal.IPlayListDataProvider
import com.example.mpstorage.database.internal.factory.AudioDataProviderFactory
import com.example.mpstorage.database.internal.factory.PlayListDataProviderFactoryImpl

object DataBaseApi {

    fun forAudio(context: Context): IAudioDataProvider{
        return AudioDataProviderFactory.create(context)
    }

    fun forPlayList(context: Context): IPlayListDataProvider{
        return PlayListDataProviderFactoryImpl.create(context)
    }
}