package com.example.mpstorage.database.internal

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mpstorage.database.internal.entity.AudioEntity
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

    @Before
    fun setup(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DataBase::class.java
        ).allowMainThreadQueries().build()
        audioDao = db.getAudioDao()
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
}