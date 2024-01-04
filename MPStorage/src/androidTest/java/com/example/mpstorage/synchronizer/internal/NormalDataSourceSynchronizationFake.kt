package com.example.mpstorage.synchronizer.internal

import android.content.Context
import com.example.mpdataprovider.contentprovider.data.MPAudio
import com.example.mpdataprovider.contentprovider.data.MissingPermissionException

object NormalDataSourceSynchronizationFake: IDataSourceForSynchronization {
    override suspend fun getAudioList(context: Context): List<MPAudio> {
        /*
        return listOf(
            MPAudio(
                id = 1L,
                artistName = "artist1",
                album = "album1",
                songName = "name1"
            ),
            MPAudio(
                id = 2L,
                artistName = "artist2",
                album = "album2",
                songName = "name2"
            )
        )
        */
        //return emptyList()
        throw MissingPermissionException(listOf("test"))
    }
}