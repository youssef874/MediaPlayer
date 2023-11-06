package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.repository.IAudioRepository
import com.example.mplog.MPLogger

object SongEditUseCase: ISongEditUseCase {

    private lateinit var audioRepository: IAudioRepository
    private val favoriteChangesListeners = mutableListOf<(UiAudio)->Unit>()
    private const val CLASS_NAME = "SongFavoriteStatus"
    private const val TAG = "AUDIO"

    operator fun invoke(audioRepository: IAudioRepository){
        this.audioRepository = audioRepository
    }

    override suspend fun changeFavoriteStatus(context: Context, uiAudio: UiAudio) {
        MPLogger.d(CLASS_NAME,"changeFavoriteStatus", TAG,"uiAudio: $uiAudio")
    }

    override fun onFavoriteChangesForAudio(onFavoriteChange: (UiAudio) -> Unit) {
        favoriteChangesListeners.add(onFavoriteChange)
    }
}