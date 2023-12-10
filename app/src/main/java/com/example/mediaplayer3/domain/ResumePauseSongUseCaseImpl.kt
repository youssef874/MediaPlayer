package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mediaplayer3.ui.uiAudioFun0
import com.example.mplog.MPLogger

object ResumePauseSongUseCaseImpl: IAudioPauseOrResumeUseCase {

    private const val CLASS_NAME = "ResumePauseSongUseCaseImpl"
    private const val TAG = "AUDIO"

    private var audioDataRepository: IAudioDataRepo? = null
    private var playUseCase: IPlayAudioUseCase = PlayAudioUseCase

    private val audioPauseListeners = mutableListOf<(UiAudio)->Unit>()
    private val audioResumeSuccessListeners = mutableListOf<uiAudioFun0>()
    private val audioResumeFailedListeners = mutableListOf<uiAudioFun0>()

    operator fun invoke(audioDataRepository: IAudioDataRepo, playUseCase: IPlayAudioUseCase){
        this.audioDataRepository = audioDataRepository
        this.playUseCase = playUseCase
    }

    override fun pauseSong(context: Context, uiAudio: UiAudio) {
        MPLogger.d(CLASS_NAME,"pauseSong", TAG,"try pause uiAudio: $uiAudio")
        playUseCase.currentPlayingSong()?.let {
            if (it.id == uiAudio.id){
                MPLogger.d(CLASS_NAME,"pauseSong", TAG,"pause the song")
                audioDataRepository?.pauseSong(context)
                audioPauseListeners.forEach { function ->
                    function(uiAudio)
                }
            }else{
                MPLogger.w(CLASS_NAME,"pauseSong", TAG,"this song was not playing to pause: ${uiAudio.id}")
            }
        }?:run {
            MPLogger.e(CLASS_NAME,"pauseSong", TAG,"There was no playing song to pause")
        }
    }

    override fun resumeSong(context: Context, uiAudio: UiAudio, seekTo: Int) {
       MPLogger.d(CLASS_NAME,"resumeSong", TAG,"try to resume song: $uiAudio, at: $seekTo")
        playUseCase.currentPlayingSong()?.let { 
            if (it.id == uiAudio.id){
                MPLogger.d(CLASS_NAME,"resumeSong", TAG," resume song: $uiAudio, at: $seekTo")
                audioDataRepository?.resumeSong(context, seekTo)
                audioResumeSuccessListeners.forEach { function ->
                    function(uiAudio,context)
                }
            }else{
                MPLogger.w(CLASS_NAME,"resumeSong", TAG,"not same as the currentSong: $uiAudio")
                audioResumeFailedListeners.forEach { function -> 
                    function(uiAudio,context)
                }
            }
        }?:run {
            MPLogger.w(
                CLASS_NAME,"resumeSong",
                TAG,"here no playing song request to play this song: ${uiAudio.id}, at: $seekTo")
            playUseCase.playSong(context, uiAudio, seekTo = seekTo)
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