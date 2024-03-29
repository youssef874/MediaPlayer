package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow

interface IPlayAudioUseCase {

    val isPlaying: Boolean

    val currentSongProgression: Int

    fun playSong(context: Context,uiAudio: UiAudio,seekTo: Int = -1)

    fun stopSong(context: Context)

    fun updatePlyingStatus(isPlaying: Boolean)

    suspend fun setOnPlaySongListener(
        onPlaySongSuccess:  (UiAudio)->Unit,
        onPlaySongFailed:  (UiAudio) -> Unit,
        job: Job?
    )

    suspend fun setOnPlaySongListener(
        onPlaySongSuccess:  (UiAudio)->Unit,
        onPlaySongFailed:  (UiAudio) -> Unit,
        predicate: ()->Boolean
    )

    suspend fun setOnStopListener(
        job: Job?,
        onSongStopped:  (UiAudio) -> Unit
    )

    suspend fun setOnStopListener(
        predicate: ()->Boolean,
        onSongStopped:  (UiAudio) -> Unit
    )

    fun songProgression(coroutineScope: CoroutineScope): SharedFlow<Int>

    fun lastSongProgress(context: Context): SharedFlow<Int>

    fun currentPlayingSong(): UiAudio?

}