package com.example.mediaplayer3.domain

import android.content.Context
import android.util.Log
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.repository.IAudioRepository
import com.example.mediaplayer3.ui.uiAudioFun0
import com.example.mplog.MPLogger


object AudioPauseOrResumeUseCase: IAudioPauseOrResumeUseCase {

    private const val CLASS_NAME = "AudioPauseOrResumeUseCase"
    private const val TAG = "AUDIO"
    private lateinit var audioRepository: IAudioRepository
    private val audioPauseListeners = mutableListOf<(UiAudio)->Unit>()
    private val audioResumeSuccessListeners = mutableListOf<uiAudioFun0>()
    private val audioResumeFailedListeners = mutableListOf<uiAudioFun0>()

    operator fun invoke(audioRepository: IAudioRepository){
        this.audioRepository = audioRepository
    }

    override fun pauseSong(context: Context, uiAudio: UiAudio) {
        MPLogger.d(CLASS_NAME,"pauseSong", TAG,"try to pause: ${uiAudio.id}")
        AudioPlayUseCase.getCurrentPlayingSong()?.let {
            if (it.id == uiAudio.id){
                MPLogger.d(CLASS_NAME,"pauseSong", TAG,"pause the song")
                audioRepository.pauseSong(context)
                audioPauseListeners.forEach {function ->
                    function(uiAudio)
                }
            }else{
                MPLogger.w(CLASS_NAME,"pauseSong", TAG,"song not playing to pause: ${uiAudio.id}")
            }
        }?:run {
            MPLogger.w(CLASS_NAME,"pauseSong", TAG,"there no playing song")
        }
    }

    override fun resumeSong(context: Context, uiAudio: UiAudio, seekTo: Int) {
        MPLogger.d(CLASS_NAME,"resumeSong",TAG,"try to resume: ${uiAudio.id}")
        AudioPlayUseCase.getCurrentPlayingSong()?.let {
            if (it.id == uiAudio.id){
                MPLogger.d(CLASS_NAME,"resumeSong",TAG,"resume: ${uiAudio.id} at seekTo: $seekTo")
                audioRepository.resumeSong(context, seekTo)
                audioResumeSuccessListeners.forEach {function ->
                    function(uiAudio,context)
                }
            }else{
                MPLogger.w(CLASS_NAME,"resumeSong",TAG,"not same as the current song: ${uiAudio.id}")
                audioResumeFailedListeners.forEach { function ->
                    function(uiAudio,context)
                }
            }
        }?:run {
            MPLogger.w(CLASS_NAME,"resumeSong",TAG,"here no playing song request to play this song: ${uiAudio.id}, at: $seekTo")
            AudioPlayUseCase.playSong(context, uiAudio, seekTo)
        }
    }

    override fun setOnAudioPauseListener(onAudioPaused: (UiAudio) -> Unit) {
        audioPauseListeners.add(onAudioPaused)
    }

    override fun setOnAudioResumeListener(
        onAudioResumeSuccess: (UiAudio, Context) -> Unit,
        onAudioResumeFailed: (UiAudio, Context) -> Unit
    ) {
        audioResumeFailedListeners.add(onAudioResumeFailed)
        audioResumeSuccessListeners.add(onAudioResumeSuccess)
    }
}