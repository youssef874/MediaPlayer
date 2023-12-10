package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.data.entity.RepeatMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IAudioConfiguratorUseCase {

    fun changePlayNextOrPreviousMode(context: Context,isRandom: Boolean)

    fun isRandomModeInFlow(context: Context): SharedFlow<Boolean>

    suspend fun changeRepeatMode(context: Context, repeatMode: RepeatMode)

    fun getRepeatMode(context: Context): Flow< RepeatMode>
}