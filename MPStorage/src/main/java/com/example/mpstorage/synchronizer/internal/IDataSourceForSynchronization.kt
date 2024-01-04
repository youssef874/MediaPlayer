package com.example.mpstorage.synchronizer.internal

import android.content.Context
import com.example.mpdataprovider.contentprovider.data.MPAudio

internal interface IDataSourceForSynchronization {

    suspend fun getAudioList(context: Context): List<MPAudio>
}