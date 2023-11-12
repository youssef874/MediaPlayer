package com.example.mpstorage.database.data

sealed interface Search

sealed class SearchAudio: Search {

    data class SearchByArtist(val artist: String): SearchAudio()

    data class SearchBySongName(val songName: String): SearchAudio()

    data class SearchByAlbum(val album: String): SearchAudio()
}