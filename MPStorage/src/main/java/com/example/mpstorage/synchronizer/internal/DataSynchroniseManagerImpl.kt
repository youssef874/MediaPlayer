package com.example.mpstorage.synchronizer.internal

import android.content.Context
import com.example.mpdataprovider.datastore.AudioDataStoreApi
import com.example.mpeventhandler.MPEventHandlerApi
import com.example.mplog.MPLogger
import com.example.mpstorage.synchronizer.event.SynchronisationChanges
import com.example.mpstorage.synchronizer.event.SynchronisationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal object DataSynchroniseManagerImpl : IDataSynchronizeManager {

    private const val CLASS_NAME = "DataSynchroniseManagerImpl"
    private const val TAG = "SYNCHRONIZATION"

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var job: Job? = null

    override fun synchronize(vararg dataSynchronize: IDataSynchronize, context: Context) {
        job = coroutineScope.launch {
            MPLogger.i(CLASS_NAME, "synchronize", TAG, "startSynchronization")
            handleSynchronisationEvents(dataSynchronize, context)
            MPEventHandlerApi.dispatchEvent(SynchronisationChanges(SynchronisationType.SYNCHRONISATION_STARTED))
            AudioDataStoreApi.isSynchronisationFinished(context).getValue().collectLatest {
                MPLogger.i(CLASS_NAME, "synchronize", TAG, "isSynchronisationFinished: $it")
                if (it){
                    MPEventHandlerApi.dispatchEvent(SynchronisationChanges(SynchronisationType.SYNCHRONISATION_COMPLETED))
                    job?.cancel()
                }else{
                    dataSynchronize.forEach {item->
                        MPLogger.i(CLASS_NAME, "synchronize", TAG, "synchronisation in progress")
                        item.synchronize(context)
                    }
                }
                return@collectLatest
            }
        }
    }
    private suspend fun handleSynchronisationEvents(
        dataSynchronize: Array<out IDataSynchronize>,
        context: Context
    ) {
        var completeCounter = 0
        dataSynchronize.forEach {
            it.setOnSynchronizeFailed {
                MPLogger.i(CLASS_NAME, "handleSynchronisationEvents", TAG, "synchronisation failed")
                MPEventHandlerApi.dispatchEvent(SynchronisationChanges(SynchronisationType.SYNCHRONIZATION_FAILED))
                AudioDataStoreApi.isSynchronisationFinished(context).updateValue(false)
                return@setOnSynchronizeFailed true
            }
            it.setOnSynchronizeCompletedListener {
                completeCounter++
                if (completeCounter == dataSynchronize.size) {
                    MPLogger.i(CLASS_NAME, "handleSynchronisationEvents", TAG, "synchronisation complete")
                    MPEventHandlerApi.dispatchEvent(SynchronisationChanges(SynchronisationType.SYNCHRONISATION_COMPLETED))
                    AudioDataStoreApi.isSynchronisationFinished(context).updateValue(true)
                }
                return@setOnSynchronizeCompletedListener true
            }
        }

    }
}