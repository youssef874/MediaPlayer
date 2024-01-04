package com.example.mpstorage.synchronizer.internal

import android.content.Context

class FakeDataSynchronize(private val isSuccess: Boolean): IDataSynchronize {

    private var onComplete: (syncFun)? = null
    private var onFailed: (syncFun)? = null
    override suspend fun synchronize(context: Context) {
        if (isSuccess){
            onComplete?.invoke()
        }else{
            onFailed?.invoke()
        }
    }

    override fun setOnSynchronizeStartedListener(onSynchronizeStarted: syncFun) {
        //TODO("Not yet implemented")
    }

    override fun setOnSynchronizeCompletedListener(onSynchronizeCompleted: syncFun) {
        onComplete = onSynchronizeCompleted
    }

    override fun setOnSynchronizeFailed(onSynchronizeFailed: syncFun) {
        onFailed = onSynchronizeFailed
    }
}