package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mplog.MPLogger

object EditSongUseCase: IEditSongUseCase {

    private const val CLASS_NAME = "EditSongUseCase"
    private const val TAG = "AUDIO"

    private var audioDataRepo: IAudioDataRepo? = null

    operator fun invoke(audioDataRepo: IAudioDataRepo){
        this.audioDataRepo = audioDataRepo
    }

    override suspend fun changeIsFavoriteStatus(
        context: Context,
        songId: Long,
        isFavorite: Boolean
    ) {
        MPLogger.d(CLASS_NAME,"changeIsFavoriteStatus", TAG,"songId: $songId, isFavorite: $isFavorite")
        audioDataRepo?.changeIsFavoriteStatusToSong(context, songId, isFavorite)
    }
}