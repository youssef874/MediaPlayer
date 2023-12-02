package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mpstorage.synchronizer.event.SynchronisationChanges
import kotlinx.coroutines.flow.SharedFlow

interface IAudioSyncUseCase {

    val syncChanges: SharedFlow<SynchronisationChanges>?
    fun sync(context: Context)
}