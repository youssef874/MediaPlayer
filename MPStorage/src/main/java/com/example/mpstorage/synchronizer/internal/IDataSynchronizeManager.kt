package com.example.mpstorage.synchronizer.internal

import android.content.Context

internal interface IDataSynchronizeManager {

    fun synchronize(vararg dataSynchronize: IDataSynchronize,context: Context)

    fun listenToSynchronizeComplete(onSynchronizeComplete: syncFun)

    fun listenToSynchroniseStarted(onSynchronizeStarted: syncFun)

    fun listenToSynchronizeFailed(onSynchronizeFailed: syncFun)
}