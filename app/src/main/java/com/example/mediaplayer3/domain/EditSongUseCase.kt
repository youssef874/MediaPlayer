package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mplog.MPLogger
import javax.inject.Inject

class EditSongUseCase @Inject constructor(private val audioDataRepo: IAudioDataRepo) :
    IEditSongUseCase {

    companion object {
        private const val CLASS_NAME = "EditSongUseCase"
        private const val TAG = "AUDIO"
    }

    override suspend fun changeIsFavoriteStatus(
        context: Context,
        songId: Long,
        isFavorite: Boolean
    ) {
        MPLogger.d(
            CLASS_NAME,
            "changeIsFavoriteStatus",
            TAG,
            "songId: $songId, isFavorite: $isFavorite"
        )
        audioDataRepo.changeIsFavoriteStatusToSong(context, songId, isFavorite)
    }
}