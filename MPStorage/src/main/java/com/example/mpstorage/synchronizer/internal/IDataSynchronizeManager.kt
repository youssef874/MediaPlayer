package com.example.mpstorage.synchronizer.internal

import android.content.Context

internal interface IDataSynchronizeManager {

    fun synchronize(vararg dataSynchronize: IDataSynchronize,context: Context)
}