package com.example.mpstorage.synchronizer.internal

import android.content.Context
import com.example.mpdataprovider.contentprovider.AudioApi
import com.example.mpdataprovider.contentprovider.data.MPAudio

internal object DataSourceForSynchronization: IDataSourceForSynchronization {


    override suspend fun getAudioList(context: Context): List<MPAudio> {
        return AudioApi.getAllSongs(context)
    }
}