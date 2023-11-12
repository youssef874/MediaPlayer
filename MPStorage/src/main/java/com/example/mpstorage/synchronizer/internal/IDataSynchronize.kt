package com.example.mpstorage.synchronizer.internal

import android.content.Context

typealias syncFun= ()->Boolean

internal interface IDataSynchronize {

    suspend fun synchronize(context: Context)

    fun setOnSynchronizeStartedListener(onSynchronizeStarted: syncFun)

    fun setOnSynchronizeCompletedListener(onSynchronizeCompleted: syncFun)

    fun setOnSynchronizeFiled(onSynchronizeFailed: syncFun)
}