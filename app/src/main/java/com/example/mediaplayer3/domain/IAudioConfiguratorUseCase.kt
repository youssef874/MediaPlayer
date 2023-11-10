package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mpdataprovider.datadtore.RepeatMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IAudioConfiguratorUseCase {

    fun changePlayNextOrPreviousMode(context: Context,isRandom: Boolean)

    fun isRandomModeInFlow(context: Context): SharedFlow<Boolean>

    suspend fun changeRepeatMode(context: Context,@RepeatMode repeatMode: Int)

    fun getRepeatMode(context: Context): Flow<@RepeatMode Int>
}