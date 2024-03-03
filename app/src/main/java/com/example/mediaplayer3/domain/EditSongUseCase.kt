package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiPlayList
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mediaplayer3.repository.toMPAppPlayList
import com.example.mpcore.api.log.MPLog
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
        MPLog.d(
            CLASS_NAME,
            "changeIsFavoriteStatus",
            TAG,
            "songId: $songId, isFavorite: $isFavorite"
        )
        audioDataRepo.changeIsFavoriteStatusToSong(context, songId, isFavorite)
    }

    override suspend fun attachSongToPlayList(
        context: Context,
        songId: Long,
        playList: UiPlayList
    ) {
        MPLog.i(CLASS_NAME,"attachSongToPlayList", TAG,"songId: $songId, playList: $playList")
        audioDataRepo.attachPlaylistToSong(context,songId,playList.toMPAppPlayList())
    }
}