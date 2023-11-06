package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.repository.IAudioRepository
import com.example.mplog.MPLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

object AudioConfigurationUseCase: IAudioConfiguratorUseCase {

    private lateinit var audioRepository: IAudioRepository
    private lateinit var coroutineScope: CoroutineScope
    private const val CLASS_NAME = "AudioConfigurationUseCase"
    private const val TAG = "AUDIO"

    operator fun invoke(audioRepository: IAudioRepository,coroutineScope: CoroutineScope){
        this.audioRepository = audioRepository
        this.coroutineScope = coroutineScope
    }



    override fun changePlayNextOrPreviousMode(context: Context, isRandom: Boolean) {
        MPLogger.d(CLASS_NAME,"changePlayNextOrPreviousMode", TAG,"isRandom: $isRandom")
        audioRepository.changePlayNextOrPreviousMode(context,isRandom)
    }

    override fun isRandomModeInFlow(context: Context): SharedFlow<Boolean> {
        return audioRepository.getIsInRandomMode(context).shareIn(coroutineScope, SharingStarted.WhileSubscribed())
    }

    override suspend fun changeRepeatMode(context: Context, repeatMode: Int) {
        MPLogger.d(CLASS_NAME,"changeRepeatMode", TAG,"repeatMode: $repeatMode")
        audioRepository.changeRepeatMode(context, repeatMode)
    }

    override fun getRepeatMode(context: Context): Flow<Int> {
        return audioRepository.getRepeatMode(context)
    }


}