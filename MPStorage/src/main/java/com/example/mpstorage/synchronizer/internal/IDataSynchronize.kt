package com.example.mpstorage.synchronizer.internal

import android.content.Context

typealias syncFun= suspend ()->Boolean

internal interface IDataSynchronize {

    suspend fun synchronize(context: Context)

    fun setOnSynchronizeStartedListener(onSynchronizeStarted: syncFun)

    fun setOnSynchronizeCompletedListener(onSynchronizeCompleted: syncFun)

    fun setOnSynchronizeFailed(onSynchronizeFailed: syncFun)
}