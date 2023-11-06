package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio

interface IAudioPauseOrResumeUseCase {

    fun pauseSong(context: Context, uiAudio: UiAudio)

    fun resumeSong(context: Context, uiAudio: UiAudio, seekTo: Int = -1)

    fun setOnAudioPauseListener(onAudioPaused: (UiAudio) -> Unit)

    fun setOnAudioResumeListener(
        onAudioResumeSuccess: (UiAudio,Context) -> Unit,
        onAudioResumeFailed: (UiAudio,Context) -> Unit
    )
}