package com.example.mpstorage.database.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mpstorage.database.data.DBPlayListData
import com.example.mpstorage.database.data.PlayListQuery
import com.example.mpstorage.database.data.SearchPlayList
import com.example.mpstorage.database.internal.entity.AudioEntity
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayListDataProviderImplTest{

    private val playListDataProvider = PlayListDataProviderImpl(FakePlayListDao)

    private val dbPlayList = DBPlayListData(
        playListId = 1L,
        playListName = "play list 1"
    )

    @Test
    fun test_Add_and_getById() = runTest {
        playListDataProvider.add(dbPlayList)
        val result = playListDataProvider.getById(1L)
        assert(result != null)
        assert(result == dbPlayList)
    }

    @Test
    fun test_update_and_FindPlayListByName()= runTest {
        playListDataProvider.add(dbPlayList)
        val updatedItem = dbPlayList.copy(playListName = "test")
        playListDataProvider.update(updatedItem)
        backgroundScope.launch {
            with(playListDataProvider.query(SearchPlayList.SearchByName("test"))){
                val single = single()
                assert(single.isNotEmpty())
            }
        }
        backgroundScope.launch {
            with(playListDataProvider.query(SearchPlayList.SearchByName("play list 1"))){
                val single = single()
                assert(single.isEmpty())
            }
        }
    }

    @Test
    fun test_delete_and_getAll()= runTest {
        playListDataProvider.add(dbPlayList)
        playListDataProvider.delete(dbPlayList)
        with(playListDataProvider.getAll()){
            assert(isEmpty())
        }
    }

    @Test
    fun test_AddSongToPlayList_and_SearchSongPlayLists()= runTest {
        val audioData = AudioEntity(
            id = 1L,
            album = "album1",
            uri = "",
            songName = "song1",
            artist = "artist1",
            duration = 70,
            size = 300,
            albumThumbnailUri = "",
            externalId = 1L
        )
        FakeAudioDao.addAudio(audioData)
        playListDataProvider.query(PlayListQuery.AddSongToPlayList(dbPlayList,1L))
        backgroundScope.launch {
            with(playListDataProvider.query(SearchPlayList.SearchSongPlayLists(1L))){
                val single = single()
                assert(single.isNotEmpty())
                assert(single.contains(dbPlayList))
            }
        }
    }
}