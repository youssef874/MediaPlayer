package com.example.mpstorage.database.internal.factory

import android.content.Context
import com.example.mpstorage.database.internal.AudioDataProvider
import com.example.mpstorage.database.internal.IAudioDataProvider

internal object AudioDataProviderFactory: IAudioDataProviderFactory {
    override fun create(context: Context): IAudioDataProvider {

        return AudioDataProvider(AudioDaoFactory.create(context))
    }
}