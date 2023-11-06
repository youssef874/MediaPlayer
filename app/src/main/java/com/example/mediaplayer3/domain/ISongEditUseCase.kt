package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio

interface ISongEditUseCase {

    suspend fun changeFavoriteStatus(
        context: Context,
        uiAudio: UiAudio
    )

    fun onFavoriteChangesForAudio(onFavoriteChange: (UiAudio)->Unit)
}