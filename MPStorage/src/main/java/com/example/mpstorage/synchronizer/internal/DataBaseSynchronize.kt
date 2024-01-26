package com.example.mpstorage.synchronizer.internal

import android.content.Context
import com.example.mpdataprovider.contentprovider.data.MissingPermissionException
import com.example.mpdataprovider.datastore.AudioDataStoreApi
import com.example.mplog.MPLogger
import com.example.mpstorage.database.data.DBAudioData
import com.example.mpstorage.database.internal.IAudioDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.sql.SQLException

internal class DataBaseSynchronize(
    private val audioDataProvider: IAudioDataProvider,
    private val dataSourceForSynchronization: IDataSourceForSynchronization
) : IDataSynchronize {

    private val onSynchronizeStartedListeners = mutableListOf<syncFun>()
    private val onSynchronizeCompletedListeners = mutableListOf<syncFun>()
    private val onSynchronizeFailedListeners = mutableListOf<syncFun>()

    override suspend fun synchronize(context: Context) {
        withContext(Dispatchers.IO) {
            MPLogger.i(CLASS_NAME, "synchronize", TAG, "start synchronization")
            onSynchronizeStartedListeners.forEach {
                if (it()) {
                    onSynchronizeStartedListeners.remove { it() }
                }
            }
            with(audioDataProvider.getAll()) {
                try {
                    if (isEmpty() && dataSourceForSynchronization.getAudioList(context).isNotEmpty()) {
                        MPLogger.i(
                            CLASS_NAME,
                            "synchronize",
                            TAG,
                            "no data in database need synchronization"
                        )
                        AudioDataStoreApi.isSynchronisationWithContentProviderCompleted(context)
                            .updateValue(false)
                        async { loadData(context, true) }.await()
                        AudioDataStoreApi.isSynchronisationWithContentProviderCompleted(context)
                            .updateValue(true)
                        onSynchronizeCompletedListeners.forEach {
                            if (it()) {
                                onSynchronizeCompletedListeners.remove {
                                    it()
                                }
                            }
                        }
                    } else if (dataSourceForSynchronization.getAudioList(context).isEmpty()) {
                        AudioDataStoreApi.isSynchronisationWithContentProviderCompleted(context)
                            .updateValue(true)
                        onSynchronizeCompletedListeners.forEach {
                            if (it()) {
                                onSynchronizeCompletedListeners.remove {
                                    it()
                                }
                            }
                        }
                    } else {
                        AudioDataStoreApi.isSynchronisationWithContentProviderCompleted(context)
                            .getValue().collectLatest {
                            if (!it) {
                                MPLogger.i(
                                    CLASS_NAME,
                                    "synchronize",
                                    TAG,
                                    "the synchronization did not complete or there a changes in preference"
                                )
                                async { loadData(context, false) }.await()
                                AudioDataStoreApi.isSynchronisationWithContentProviderCompleted(context)
                                    .updateValue(true)
                                onSynchronizeCompletedListeners.forEach {
                                    if (it()) {
                                        onSynchronizeCompletedListeners.remove {
                                            it()
                                        }
                                    }
                                }
                            } else {
                                onSynchronizeCompletedListeners.forEach {
                                    if (it()) {
                                        onSynchronizeCompletedListeners.remove {
                                            it()
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: MissingPermissionException) {
                    MPLogger.e(CLASS_NAME, "synchronize", TAG, "message: ${e.message}")
                    onSynchronizeFailedListeners.forEach {
                        if (it()) {
                            onSynchronizeFailedListeners.remove {
                                it()
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadData(context: Context, isEmpty: Boolean) {
        try {
            if (isEmpty) {
                dataSourceForSynchronization.getAudioList(context).forEach {
                    MPLogger.d(CLASS_NAME, "loadData", TAG, "song: $it")
                    audioDataProvider.add(
                        DBAudioData(
                            album = it.album,
                            uri = it.uri,
                            songName = it.songName,
                            artist = it.artistName,
                            duration = it.duration,
                            size = it.size,
                            albumThumbnailUri = it.albumThumbnailUri,
                            externalId = it.id,
                            isFavorite = false
                        )
                    )
                }
            } else {
                dataSourceForSynchronization.getAudioList(context).forEach { mpAudio ->
                    audioDataProvider.getAll().find { it.externalId == mpAudio.id }?.let {
                        MPLogger.d(CLASS_NAME, "loadData", TAG, "song: $it")
                        audioDataProvider.update(
                            DBAudioData(
                                album = mpAudio.album,
                                uri = mpAudio.uri,
                                songName = mpAudio.songName,
                                artist = mpAudio.artistName,
                                duration = mpAudio.duration,
                                size = mpAudio.size,
                                albumThumbnailUri = mpAudio.albumThumbnailUri,
                                externalId = mpAudio.id,
                                idAudio = it.idAudio,
                                isFavorite = false
                            )
                        )
                    } ?: run {
                        MPLogger.d(CLASS_NAME, "loadData", TAG, "song: $mpAudio")
                        audioDataProvider.add(
                            DBAudioData(
                                album = mpAudio.album,
                                uri = mpAudio.uri,
                                songName = mpAudio.songName,
                                artist = mpAudio.artistName,
                                duration = mpAudio.duration,
                                size = mpAudio.size,
                                albumThumbnailUri = mpAudio.albumThumbnailUri,
                                externalId = mpAudio.id,
                                isFavorite = false
                            )
                        )
                    }
                }
            }
            onSynchronizeCompletedListeners.forEach {
                if (it()) {
                    onSynchronizeCompletedListeners.remove {
                        it()
                    }
                }
            }
        } catch (e: SQLException) {
            MPLogger.e(CLASS_NAME, "synchronize", TAG, "message: ${e.message}")
            onSynchronizeFailedListeners.forEach {
                if (it()) {
                    onSynchronizeFailedListeners.remove {
                        it()
                    }
                }
            }
        }
    }

    override fun setOnSynchronizeStartedListener(onSynchronizeStarted: syncFun) {
        onSynchronizeStartedListeners.add(onSynchronizeStarted)
    }

    override fun setOnSynchronizeCompletedListener(onSynchronizeCompleted: syncFun) {
        onSynchronizeCompletedListeners.add(onSynchronizeCompleted)
    }

    override fun setOnSynchronizeFailed(onSynchronizeFailed: syncFun) {
        onSynchronizeFailedListeners.add(onSynchronizeFailed)
    }

    companion object {
        private const val CLASS_NAME = "DataBaseSynchronize"
        private const val TAG = "SYNCHRONIZATION"
    }
}