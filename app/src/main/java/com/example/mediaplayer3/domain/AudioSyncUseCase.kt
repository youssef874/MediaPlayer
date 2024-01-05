package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mpstorage.synchronizer.event.SynchronisationChanges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class AudioSyncUseCase @Inject constructor(private val audioDataRepo: IAudioDataRepo): IAudioSyncUseCase, IUseCase {

    private var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    override val scope: CoroutineScope
        get() = coroutineScope
    override val syncChanges: SharedFlow<SynchronisationChanges>
        get() = audioDataRepo.observeSynchronisationChanges().shareIn(scope, started = SharingStarted.WhileSubscribed())

    override fun sync(context: Context) {
        audioDataRepo.synchronization(context)
    }
}