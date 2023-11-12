package com.example.mpstorage.database.data

import android.net.Uri

data class DBAudioData(
    val idAudio: Long = 0,
    val album: String = "",
    val uri: Uri = Uri.EMPTY,
    val songName: String = "",
    val artist: String = "",
    val duration: Int = -1,
    val size: Int = -1,
    val albumThumbnailUri: Uri? = null,
    val isFavorite: Boolean = true,
    val isInternal: Boolean = true,
    val isOwned: Boolean = true,
    val externalId: Long = 0
): BaseDatabaseData(idAudio)