package com.example.mpstorage.database.internal

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mpstorage.database.data.DBAudioData
import com.example.mpstorage.database.data.QueryAudio
import com.example.mpstorage.database.data.SearchAudio
import com.example.mpstorage.database.internal.entity.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioDataProviderTest{

    private val audioDataProvider: AudioDataProvider = AudioDataProvider(FakeAudioDao)

    private val dbAudioData = DBAudioData(
        idAudio = 1L,
        album = "album1",
        uri = Uri.parse(""),
        songName = "song1",
        artist = "artist1",
        duration = 70,
        size = 300,
        albumThumbnailUri = null,
        externalId = 1L
    )

    @Test
    fun test_add() = runTest {
        audioDataProvider.add(dbAudioData)
        backgroundScope.launch {
            with(audioDataProvider.getAll()){
                assert(isNotEmpty())
                assert(contains(dbAudioData))
            }
        }
    }

    @Test
    fun tes_update()= runTest {
        audioDataProvider.add(dbAudioData)
        val updated = dbAudioData.copy(isOwned = false)
        audioDataProvider.update(updated)
        backgroundScope.launch {
            with(audioDataProvider.observeById(updated.idAudio)){
                assert(count() == 1)
                assert(single()?.isOwned == false)
            }
        }
    }

    @Test
    fun test_search_query() = runTest {
        audioDataProvider.add(dbAudioData)
        backgroundScope.launch {
            with(audioDataProvider.query(SearchAudio.SearchByArtist("artist1"))){
                val list = single()
                assert(list.isNotEmpty())
                assert(list.find { it.artist == "artist1" } != null)
            }

            with(audioDataProvider.query(SearchAudio.SearchByArtist("artistZ"))){
                val list = single()
                assert(list.isEmpty())
                assert(list.find { it.artist == "artist1" } == null)
            }
            with(audioDataProvider.query(SearchAudio.SearchByAlbum("album1"))){
                val list = single()
                assert(list.isNotEmpty())
                assert(list.find { it.album == "album1" } != null)
            }

            with(audioDataProvider.query(SearchAudio.SearchByAlbum("album2"))){
                val list = single()
                assert(list.isEmpty())
                assert(list.find { it.artist == "album1" } == null)
            }
        }
    }

    @Test
    fun test_changeIsFavorite()= runTest {
        audioDataProvider.add(dbAudioData)
        audioDataProvider.query(QueryAudio.ChaneIsFavorite(dbAudioData.idAudio,true))
        backgroundScope.launch {
            with(audioDataProvider.observeById(dbAudioData.idAudio)){
                assert(count() == 1)
                assert(single()?.isFavorite == true)
            }
        }
    }

    @Test
    fun test_SearchForAllSongForPlayList()= runTest {
        audioDataProvider.add(dbAudioData)
        FakePlayListDao.addPlayList(
            PlayListEntity(id = 1L, name = "play list 1")
        )
        FakePlayListDao.addPlaylistSongCrossRef(PlaylistSongCrossRef(playListId = 1L, songId = 1L))
        backgroundScope.launch {
            with(audioDataProvider.query(SearchAudio.SearchForAllSongForPlayList(playListId = 1L))){
                val single = single()
                assert(single.isNotEmpty())
                assert(single.contains(dbAudioData))
            }
        }
    }
}