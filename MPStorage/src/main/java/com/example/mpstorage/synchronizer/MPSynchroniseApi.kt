package com.example.mpstorage.synchronizer

import android.content.Context
import com.example.mpstorage.synchronizer.factory.DataSynchroniseManagerFactoryImpl

/**
 * This calls responsible in synchronization like from content provider to DB
 */
object MPSynchroniseApi {

    /**
     * Call this method to listen to SynchroniseStartedEvent
     * @param context: Android context
     * @param onSynchronizeStarted: Event callback once it is invoked the event will unsubscribe
     */
    fun listenToSynchroniseStartedEvent(context: Context, onSynchronizeStarted: ()->Unit){
        DataSynchroniseManagerFactoryImpl.create(context).listenToSynchroniseStarted {
            onSynchronizeStarted()
            return@listenToSynchroniseStarted true
        }
    }

    /**
     * Call this method to listen to SynchroniseCompletedEvent
     * @param context: Android context
     * @param onSynchroniseCompleted: Event callback once it is invoked the event will unsubscribe
     */
    fun listenToSynchroniseCompletedEvent(context: Context, onSynchroniseCompleted: ()->Unit){
        DataSynchroniseManagerFactoryImpl.create(context).listenToSynchronizeComplete {
            onSynchroniseCompleted()
            return@listenToSynchronizeComplete true
        }
    }

    /**
     * Call this method to listen to SynchroniseFailedEvent
     * @param context: Android context
     * @param onSynchronisationFailed: Event callback once it is invoked the event will unsubscribe
     */
    fun listenToSynchroniseFailedEvent(context: Context, onSynchronisationFailed: ()->Unit){
        DataSynchroniseManagerFactoryImpl.create(context).listenToSynchronizeFailed {
            onSynchronisationFailed()
            return@listenToSynchronizeFailed true
        }
    }
}