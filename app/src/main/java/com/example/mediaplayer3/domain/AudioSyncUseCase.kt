package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mpstorage.synchronizer.event.SynchronisationChanges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

object AudioSyncUseCase: IAudioSyncUseCase, IUseCase {

    private var audioDataRepo: IAudioDataRepo? = null
    private var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    override val scope: CoroutineScope
        get() = coroutineScope

    operator fun invoke(audioDataRepo: IAudioDataRepo, coroutineScope: CoroutineScope): IAudioSyncUseCase{
        this.audioDataRepo = audioDataRepo
        this.coroutineScope = coroutineScope
        return  this
    }
    override val syncChanges: SharedFlow<SynchronisationChanges>?
        get() = audioDataRepo?.observeSynchronisationChanges()?.shareIn(scope, started = SharingStarted.WhileSubscribed())

    override fun sync(context: Context) {
        audioDataRepo?.synchronization(context)
    }
}