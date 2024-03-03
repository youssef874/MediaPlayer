package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.data.entity.RepeatMode
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mpcore.api.log.MPLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioConfigurationUseCaseImpl @Inject constructor(
    private val audioDataRepo: IAudioDataRepo
) : IAudioConfiguratorUseCase, IUseCase {

    companion object {
        private const val CLASS_NAME = "AudioConfigurationUseCaseImpl"
        private const val TAG = "AUDIO"
    }


    private val _scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    override val scope: CoroutineScope
        get() = _scope

    override fun changePlayNextOrPreviousMode(context: Context, isRandom: Boolean) {
        MPLog.d(CLASS_NAME, "changePlayNextOrPreviousMode", TAG, "isRandom: $isRandom")
        scope.launch {
            audioDataRepo.updateRandomMode(context, isRandom)
        }
    }

    override fun isRandomModeInFlow(context: Context): SharedFlow<Boolean> {
        return audioDataRepo.observeIsRandomMode(context)
            .shareIn(scope, SharingStarted.WhileSubscribed())
    }

    override suspend fun changeRepeatMode(context: Context, repeatMode: RepeatMode) {
        MPLog.d(CLASS_NAME, "changeRepeatMode", TAG, "repeatMode: $repeatMode")
        scope.launch {
            audioDataRepo.updateRepeatMode(context, repeatMode)
        }
    }

    override fun getRepeatMode(context: Context): Flow<RepeatMode> {
        return audioDataRepo.observeRepeatMode(context)
            .shareIn(scope, SharingStarted.WhileSubscribed())
    }
}