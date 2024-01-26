package com.example.mpstorage.database.data

sealed interface Search

sealed interface Query

sealed class SearchAudio: Search {

    data class SearchByArtist(val artist: String): SearchAudio()

    data class SearchBySongName(val songName: String): SearchAudio()

    data class SearchByAlbum(val album: String): SearchAudio()

    data class SearchForAllSongForPlayList(val playListId: Long): SearchAudio()

    data class GetFirstSongInPlaylist(val playListId: Long): SearchAudio()
}

sealed class QueryAudio: Query{

    data class ChaneIsFavorite(val songId: Long, val isFavorite: Boolean): QueryAudio()
}

sealed class SearchPlayList: Search{

    data class SearchByName(val playListName: String): SearchPlayList()

    data class SearchSongPlayLists(val songId: Long): SearchPlayList()
}

sealed class PlayListQuery: Query{

    data class AddSongToPlayList(val playList: DBPlayListData,val songId: Long): PlayListQuery()
}