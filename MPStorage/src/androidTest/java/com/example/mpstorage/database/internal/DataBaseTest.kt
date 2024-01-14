package com.example.mpstorage.database.internal

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mpstorage.database.internal.entity.AudioEntity
import com.example.mpstorage.database.internal.entity.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataBaseTest{

    private lateinit var audioDao: IAudioDao
    private lateinit var db: DataBase
    private lateinit var playListDao: IPlayListDao


    private val audio = AudioEntity(
        id = 1L,
        album = "album1",
        uri = "uri1",
        songName = "song1",
        artist = "artist1",
        duration = 50,
        size = 300,
        albumThumbnailUri = "albumThumbnailUri1",
        isFavorite = false,
        isInternal = false,
        isOwned = true,
        externalId = 1L
    )

    private val audio2 = AudioEntity(
        id = 2L,
        album = "album2",
        uri = "uri2",
        songName = "song2",
        artist = "artist2",
        duration = 70,
        size = 500,
        albumThumbnailUri = "albumThumbnailUri2",
        isFavorite = false,
        isInternal = false,
        isOwned = true,
        externalId = 2L
    )

    private val playLists = mutableListOf<PlayListEntity>(
        PlayListEntity(
            id = 1L,
            name = "playList1"
        ),
        PlayListEntity(
            id = 2L,
            name = "playList2"
        )
    )

    private val playlistSongCrossRef1 = PlaylistSongCrossRef(
        playListId = playLists.first().id,
        songId = audio.id
    )

    private val playlistSongCrossRef2 = PlaylistSongCrossRef(
        playListId = playLists.first().id,
        songId = audio2.id
    )


    private val playlistSongCrossRef3 = PlaylistSongCrossRef(
        playListId = playLists.last().id,
        songId = audio.id
    )

    private val playlistSongCrossRef4 = PlaylistSongCrossRef(
        playListId = playLists.last().id,
        songId = audio2.id
    )



    @Before
    fun setup(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DataBase::class.java
        ).allowMainThreadQueries().build()
        audioDao = db.getAudioDao()
        playListDao = db.getPlayListDao()
    }

    @After
    fun finish(){
        db.close()
    }

    @Test
    fun test_addAudio() = runTest {
        audioDao.addAudio(audio)
        backgroundScope.launch {
            with(audioDao.getAllAudios()){
                assert(isNotEmpty())
                assert(find { it.id == audio.id } != null)
            }
        }
    }

    @Test
    fun test_updateAudio()= runTest {
        audioDao.addAudio(audio)
        audioDao.updateAudio(audio.copy(isOwned = false))
        with(audioDao.getAudioById(audio.id)){
            assert(this?.isOwned  ==false)
        }
    }

    @Test
    fun test_deleteAudio()= runTest {
        audioDao.addAudio(audio)
        audioDao.addAudio(audio2)
        audioDao.deleteAudio(audio2)
        with(audioDao.getAllAudios()){
            assert(isNotEmpty())
            assert(count() == 1)
            assert(find { it.id == audio.id } != null)
            assert(find { it.id == audio2.id } == null)
        }
    }

    @Test
    fun test_getAudioBySongName()= runTest {
        audioDao.addAudio(audio)
        audioDao.addAudio(audio2)
        backgroundScope.launch {
            with(audioDao.getAudioBySongName(audio.songName)){
                val single = single()
                assert(single.count() == 1)
                assert(single.find { it.id == audio.id } != null)
                assert(single.any { it.songName == audio.songName })
                assert(single.none { it.songName == audio2.songName })

            }
        }
    }

    @Test
    fun test_getAudioByAlbum()= runTest {
        audioDao.addAudio(audio)
        audioDao.addAudio(audio2)
        backgroundScope.launch {
            with(audioDao.getAudioByAlbum(audio2.songName)){
                val single = single()
                assert(single.count() == 1)
                assert(single.find { it.id == audio2.id } != null)
                assert(single.any { it.album == audio2.album })
                assert(single.none { it.album == audio.album })

            }
        }
    }

    @Test
    fun test_getAudioArtist() = runTest {
        audioDao.addAudio(audio)
        audioDao.addAudio(audio2)
        backgroundScope.launch {
            with(audioDao.getAudioArtist(audio.songName)){
                val single = single()
                assert(single.count() == 1)
                assert(single.find { it.id == audio.id } != null)
                assert(single.any { it.artist == audio.artist })
                assert(single.none { it.artist == audio2.artist })

            }
        }
    }

    @Test
    fun test_addPlayList_and_getPlayListById()= runTest {
        val firsItem = playLists.first()
        playListDao.addPlayList(firsItem)
        val playlist = playListDao.getPlayListById(firsItem.id)
        assert(playlist != null)
        assert(playlist?.id == firsItem.id)
    }

    @Test
    fun test_addPlayList_tow_item_and_getAllPlayList()= runTest {
        playLists.forEach {
            playListDao.addPlayList(it)
        }
        val list = playListDao.getAllPlayList()
        assert(list.size == 2)
        assert(list.any { it.id == playLists.first().id })
        assert(list.any { it.id == playLists.last().id })
    }

    @Test
    fun test_addPlayList_updatePlayList_getPlayListById() = runTest {
        val firsItem = playLists.first()
        playListDao.addPlayList(firsItem)
        val updatedItem = firsItem.copy(name = "test")
        playListDao.updatePlayList(updatedItem)
        val playlist = playListDao.getPlayListById(firsItem.id)
        assert(playlist != null)
        assert(playlist?.id == firsItem.id)
        assert(playlist?.name != firsItem.name)
        assert(playlist?.name == updatedItem.name)
    }

    fun test_addPlayList_deletePlayList()= runTest {
        val firsItem = playLists.first()
        playListDao.addPlayList(firsItem)
        playListDao.deletePlayList(firsItem)
        val playlist = playListDao.getPlayListById(firsItem.id)
        assert(playlist == null)
    }

    @Test
    fun test_observePlayListsByName() = runTest {
        playLists.forEach {
            playListDao.addPlayList(it)
        }
        backgroundScope.launch {
            with(playListDao.observePlayListsByName("playList1")){
                val single = single()
                assert(single.size == 1)
                assert(single.find { it.id ==  playLists.first().id} != null)
            }
        }
    }

    @Test
    fun test_addPlaylistSongCrossRef_and_getListOfPlayListOfSongs()= runTest {
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef1)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef2)
        //playListDao.addPlaylistSongCrossRef(playlistSongCrossRef3)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef4)
        backgroundScope.launch {
            with(playListDao.getListOfPlayListOfSongs()){
                assert(isNotEmpty())
                find { it.audioEntity.id == audio.id }?.let {
                    assert(it.playLists.isNotEmpty())
                    assert(it.playLists.any { playListEntity -> playListEntity.id == playLists.first().id })
                    assert(it.playLists.none { playListEntity -> playListEntity.id == playLists.last().id })
                }

                find { it.audioEntity.id == audio2.id }?.let {
                    assert(it.playLists.isNotEmpty())
                    assert(it.playLists.any { playListEntity -> playListEntity.id == playLists.first().id })
                    assert(it.playLists.any() { playListEntity -> playListEntity.id == playLists.last().id })
                }
            }
        }
    }

    @Test
    fun test_addPlaylistSongCrossRef_and_getListOfAudioListOfPlayList() = runTest {
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef1)
        //playListDao.addPlaylistSongCrossRef(playlistSongCrossRef2)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef3)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef4)
        backgroundScope.launch {
            with(audioDao.getListOfAudioListOfPlayList()){
                assert(isNotEmpty())
                find { it.playListEntity.id == playLists.first().id}?.let { playlistWithSongs ->
                    assert(playlistWithSongs.songs.isNotEmpty())
                    assert(playlistWithSongs.songs.any { it.id == audio.id })
                    assert(playlistWithSongs.songs.none { it.id == audio2.id })
                }
                find { it.playListEntity.id == playLists.last().id}?.let { playlistWithSongs ->
                    assert(playlistWithSongs.songs.isNotEmpty())
                    assert(playlistWithSongs.songs.any { it.id == audio.id })
                    assert(playlistWithSongs.songs.any { it.id == audio2.id })
                }
            }
        }
    }

    @Test
    fun test_addPlaylistSongCrossRef_and_getListOfAudioForPlayList() = runTest {
        playLists.forEach {
            playListDao.addPlayList(it)
        }
        audioDao.addAudio(audio)
        audioDao.addAudio(audio2)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef1)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef2)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef3)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef4)

        val result = audioDao.getListOfAudioForPlayList(playLists.first().id)
        assert(result != null)
        assert(result?.songs?.size == 2)
        assert(result?.playListEntity?.id == playLists.first().id)
        assert(result?.songs?.any { it.id == audio.id } != null)
        assert(result?.songs?.any { it.id == audio2.id } != null)
    }

    @Test
    fun test_addPlaylistSongCrossRef_and_getListOfPlayListForAudio()= runTest {
        playLists.forEach {
            playListDao.addPlayList(it)
        }
        audioDao.addAudio(audio)
        audioDao.addAudio(audio2)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef1)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef2)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef3)
        playListDao.addPlaylistSongCrossRef(playlistSongCrossRef4)

        val result = playListDao.getListOfPlayListForAudio(audio.id)
        assert(result != null)
        assert(result?.playLists?.size == 2)
        assert(result?.audioEntity?.id == audio.id)
        assert(result?.playLists?.any { it.id == playLists.first().id } != null)
        assert(result?.playLists?.any { it.id == playLists.last().id } != null)
    }
}