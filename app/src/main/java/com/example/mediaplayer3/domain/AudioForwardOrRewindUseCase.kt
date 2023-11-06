package com.example.mediaplayer3.domain

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.mediaplayer3.repository.IAudioRepository
import com.example.mplog.MPLogger

object AudioForwardOrRewindUseCase: IAudioForwardOrRewindUseCase {

    private lateinit var audioRepository: IAudioRepository
    private const val CLASS_NAME = "AudioForwardOrRewindUseCase"
    private const val TAG = "AUDIO"

    operator fun invoke(audioRepository: IAudioRepository){
        this.audioRepository = audioRepository
    }

    override fun forward(forwardAt: Int) {
        AudioPlayUseCase.getCurrentPlayingSong()?.let {
            MPLogger.d(CLASS_NAME,"forward", TAG,"songId: ${it.id} at: $forwardAt")
            audioRepository.forward(forwardAt)
        }?:run {
            MPLogger.w(CLASS_NAME,"forward", TAG,"no current song to rewind")
        }
    }

    override fun rewind(rewindAt: Int) {
        AudioPlayUseCase.getCurrentPlayingSong()?.let {
            MPLogger.d(CLASS_NAME,"rewind", TAG,"songId: ${it.id} at: $rewindAt")
            audioRepository.rewind(rewindAt)
        }?:run {
            MPLogger.e(CLASS_NAME,"rewind", TAG,"no current song to forward")
        }
    }

    override fun setPlayingPosition(context: Context, uri: Uri, position: Int) {
        MPLogger.d(CLASS_NAME,"setPlayingPosition", TAG,"songUri: $uri position: $position")
        audioRepository.setPlayingPosition(context, uri, position)
    }

}