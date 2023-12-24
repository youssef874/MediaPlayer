package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio

interface IPlayNextOrPreviousSongUseCase {

    fun playNext(currentSong: UiAudio,isRandom: Boolean,context: Context)

    fun playPrevious(currentSong: UiAudio, isRandom: Boolean,context: Context)
}