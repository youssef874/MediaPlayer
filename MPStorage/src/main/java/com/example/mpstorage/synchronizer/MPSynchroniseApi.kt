package com.example.mpstorage.synchronizer

import android.content.Context
import com.example.mpeventhandler.MPEventHandlerApi
import com.example.mpeventhandler.data.MPEvent
import com.example.mpeventhandler.internal.IEventCanceler
import com.example.mpeventhandler.internal.MPEventListener
import com.example.mpstorage.synchronizer.event.SynchronisationChanges
import com.example.mpstorage.synchronizer.event.SynchronisationType
import com.example.mpstorage.synchronizer.factory.DataSynchroniseManagerFactoryImpl

/**
 * This calls responsible in synchronization like from content provider to DB
 */
object MPSynchroniseApi {

    /**
     * Call this method to synchronize data
     * @param context: Android context
     */
    fun synchronize(context: Context){
        DataSynchroniseManagerFactoryImpl.create(context)
    }

    /**
     * Call this method to subscribe to SynchronisationChanges
     * @param callback: [SynchronisationChanges]
     */
    fun subscribeToSynchronizationChanges(callback: (SynchronisationChanges)->Unit): IEventCanceler{
        val onSynchronisationStarted = SynchronisationChanges(SynchronisationType.SYNCHRONISATION_STARTED)
        val onSynchronisationCompleted = SynchronisationChanges(SynchronisationType.SYNCHRONISATION_COMPLETED)
        val onSynchronisationFailed = SynchronisationChanges(SynchronisationType.SYNCHRONIZATION_FAILED)
        return MPEventHandlerApi.subscribe(
            arrayOf(onSynchronisationStarted,onSynchronisationCompleted,onSynchronisationFailed),
            object : MPEventListener{
                override fun onEvent(event: MPEvent) {
                    if (event is SynchronisationChanges){
                        callback(event)
                    }
                }

            }
        )
    }
}