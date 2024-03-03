package com.example.mediaplayer3.domain

import android.content.Context
import android.net.Uri
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mpcore.api.log.MPLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioForwardOrRewindUseCaseImp @Inject constructor(
    private val audioDataRepo: IAudioDataRepo,
    private val playUseCase: IPlayAudioUseCase
) : IAudioForwardOrRewindUseCase, IUseCase {

    companion object {
        private const val CLASS_NAME = "AudioForwardOrRewindUseCaseImp"
        private const val TAG = "AUDIO"
    }


    private val _scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    override val scope: CoroutineScope
        get() = _scope

    override fun forward(forwardAt: Int) {
        playUseCase.currentPlayingSong()?.let {
            MPLog.d(CLASS_NAME, "forward", TAG, "forwardAt: $forwardAt")
            audioDataRepo.forward(forwardAt)
        } ?: run {
            MPLog.w(CLASS_NAME, "forward", TAG, "no current song to forward")
        }
    }

    override fun rewind(rewindAt: Int) {
        playUseCase.currentPlayingSong()?.let {
            MPLog.d(CLASS_NAME, "rewind", TAG, "rewindAt: $rewindAt")
            audioDataRepo.rewind(rewindAt)
        } ?: run {
            MPLog.w(CLASS_NAME, "rewind", TAG, "no current song to rewind")
        }
    }

    override fun setPlayingPosition(context: Context, uri: Uri, position: Int) {
        MPLog.d(CLASS_NAME, "setPlayingPosition", TAG, "uri: $uri, position: $position")
        scope.launch {
            audioDataRepo.setPlayingPosition(context, uri, position)
        }
    }
}