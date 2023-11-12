package com.example.mpstorage.synchronizer.internal

import android.content.Context
import com.example.mpdataprovider.datastore.AudioDataStoreApi
import com.example.mplog.MPLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal object DataSynchroniseManagerImpl : IDataSynchronizeManager {

    private const val CLASS_NAME = "DataSynchroniseManagerImpl"
    private const val TAG = "SYNCHRONIZATION"

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var onSynchronizeComplete: syncFun? = null
    private var onSynchronizeFailed: syncFun? = null
    private var onSynchronizeStarted: syncFun? = null

    private var job: Job? = null

    override fun synchronize(vararg dataSynchronize: IDataSynchronize, context: Context) {
        job = coroutineScope.launch {
            MPLogger.i(CLASS_NAME, "synchronize", TAG, "startSynchronization")
            AudioDataStoreApi.isSynchronisationFinished(context).getValue().collectLatest {
                MPLogger.i(CLASS_NAME, "synchronize", TAG, "isSynchronisationFinished: $it")
                if (it){
                    if (onSynchronizeComplete?.invoke() == true){
                        onSynchronizeComplete = null
                    }
                    job?.cancel()
                }
                return@collectLatest
            }
            if (onSynchronizeStarted?.invoke() == true) {
                AudioDataStoreApi.isSynchronisationFinished(context).updateValue(false)
                onSynchronizeStarted = null
            }
            dataSynchronize.forEach {
                MPLogger.i(CLASS_NAME, "synchronize", TAG, "synchronisation in progress")
                it.synchronize(context)
            }
            handleSynchronisationEvents(dataSynchronize, context)
        }
    }
    private suspend fun handleSynchronisationEvents(
        dataSynchronize: Array<out IDataSynchronize>,
        context: Context
    ) {
        var isFailed = false
        var completeCounter = 0
        dataSynchronize.forEach {
            it.setOnSynchronizeFiled {
                MPLogger.i(CLASS_NAME, "handleSynchronisationEvents", TAG, "synchronisation failed")
                if (onSynchronizeFailed?.invoke() == true) {
                    onSynchronizeFailed = null
                }
                isFailed = true
                return@setOnSynchronizeFiled true
            }
            if (isFailed) {
                AudioDataStoreApi.isSynchronisationFinished(context).updateValue(false)
                return
            }
            it.setOnSynchronizeCompletedListener {
                completeCounter++
                return@setOnSynchronizeCompletedListener true
            }
        }
        if (completeCounter == dataSynchronize.size) {
            MPLogger.i(CLASS_NAME, "handleSynchronisationEvents", TAG, "synchronisation complete")
            if (onSynchronizeComplete?.invoke() == true) {
                AudioDataStoreApi.isSynchronisationFinished(context).updateValue(true)
                onSynchronizeComplete = null
            }
        }
    }

    override fun listenToSynchronizeComplete(onSynchronizeComplete: syncFun) {
        this.onSynchronizeComplete = onSynchronizeComplete
    }

    override fun listenToSynchroniseStarted(onSynchronizeStarted: syncFun) {
        this.onSynchronizeStarted = onSynchronizeStarted
    }

    override fun listenToSynchronizeFailed(onSynchronizeFailed: syncFun) {
        this.onSynchronizeFailed = onSynchronizeFailed
    }
}