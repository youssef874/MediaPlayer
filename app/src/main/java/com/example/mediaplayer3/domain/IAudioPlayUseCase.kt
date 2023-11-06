package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow

interface IAudioPlayUseCase {

    val songProgressionJob: Job?

    fun playSong(
        context: Context,
        uiAudio: UiAudio,
        seekTo: Int = -1
    )

    fun stopSong(context: Context)

    fun getCurrentPlayingSong(): UiAudio?

    fun getLastPlayingSongDuration(context: Context): SharedFlow<Int>

    fun setOnSongPlayResultListener(onSongPlaySuccess: (UiAudio)->Unit, onSongPlayFailed: (UiAudio)-> Unit)

    fun setOnSongStopPlayListener(onSongStopPlay: (UiAudio)-> Unit)

    fun getPlayingSongDuration(context: Context): SharedFlow<Int>

    fun getLastPlayingSong(context: Context): SharedFlow<Long>
}