package com.example.mpstorage.synchronizer.factory

import android.content.Context
import com.example.mpstorage.synchronizer.internal.IDataSynchronizeManager

internal interface IDataSynchroniseManagerFactory {

    fun create(context: Context): IDataSynchronizeManager
}