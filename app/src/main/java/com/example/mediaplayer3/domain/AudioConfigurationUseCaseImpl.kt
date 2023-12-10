package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.data.entity.RepeatMode
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mplog.MPLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

object AudioConfigurationUseCaseImpl: IAudioConfiguratorUseCase,IUseCase {

    private const val CLASS_NAME = "AudioConfigurationUseCaseImpl"
    private const val TAG = "AUDIO"

    private var audioDataRepo: IAudioDataRepo? = null

    private var _scope = CoroutineScope(Dispatchers.Default)
    override val scope: CoroutineScope
        get() = _scope

    operator fun invoke(audioDataRepo: IAudioDataRepo, coroutineScope: CoroutineScope){
        this.audioDataRepo = audioDataRepo
        _scope = coroutineScope
    }
    override fun changePlayNextOrPreviousMode(context: Context, isRandom: Boolean) {
        MPLogger.d(CLASS_NAME,"changePlayNextOrPreviousMode", TAG,"isRandom: $isRandom")
        scope.launch {
            audioDataRepo?.updateRandomMode(context, isRandom)
        }
    }

    override fun isRandomModeInFlow(context: Context): SharedFlow<Boolean> {
        return audioDataRepo?.observeIsRandomMode(context)?.shareIn(scope, SharingStarted.WhileSubscribed())?:run {
            flow {
                emit(false)
            }.shareIn(scope = scope, started = SharingStarted.WhileSubscribed())
        }
    }

    override suspend fun changeRepeatMode(context: Context, repeatMode: RepeatMode) {
        MPLogger.d(CLASS_NAME,"changeRepeatMode", TAG,"repeatMode: $repeatMode")
        scope.launch {
            audioDataRepo?.updateRepeatMode(context, repeatMode)
        }
    }

    override fun getRepeatMode(context: Context): Flow<RepeatMode> {
        return audioDataRepo?.observeRepeatMode(context)
            ?.shareIn(scope, SharingStarted.WhileSubscribed()) ?: run {
            flow {
                emit(RepeatMode.NO_REPEAT)
            }.shareIn(scope = scope, started = SharingStarted.WhileSubscribed())
        }
    }
}