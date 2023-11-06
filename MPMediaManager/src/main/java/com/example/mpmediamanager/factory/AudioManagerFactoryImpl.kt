package com.example.mpmediamanager.factory

import com.example.mpmediamanager.internal.AudioPlayerManagerImpl
import com.example.mpmediamanager.internal.IAudioPlayerManager

internal object AudioManagerFactoryImpl: IAudioManagerFactory {

    @Volatile
    private var sInstance: IAudioPlayerManager? = null

    override fun create(): IAudioPlayerManager {
        return sInstance?: synchronized(this){
            val instance = AudioPlayerManagerImpl()
            sInstance = instance
            return instance
        }
    }
}