package com.example.mpdataprovider.ContentProvider.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mpdataprovider.ContentProvider.data.MPAudio
import com.example.mpdataprovider.ContentProvider.data.MissingPermissionException
import com.example.mpdataprovider.internal.FakeAudioConfigurationGranted
import com.example.mpdataprovider.internal.FakeAudioConfigurationNonGranted
import com.example.mpdataprovider.internal.FakeAudioExtractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioProviderImplTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val audioProviderGranted = AudioProviderImpl(
        audioConfiguration = FakeAudioConfigurationGranted(),
        audioExtractor = FakeAudioExtractor()
    )
    private val audioProviderNotGranted = AudioProviderImpl(
        audioConfiguration = FakeAudioConfigurationNonGranted(),
        audioExtractor = FakeAudioExtractor()
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    private val background = TestScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_allSong_granted() = runTest {
        with(audioProviderGranted.getAllSongs(context)){
            assert(isNotEmpty())
            val count = count()
            assert(count == 3)
            val id1 = find { it.id == 1L }
            val id2 = find { it.id == 2L }
            val id3 = find { it.id == 3L }
            assert(id1 != null)
            assert(id2 != null)
            assert(id3 != null)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = MissingPermissionException::class)
    fun test_allSong_with_permission_not_granted() = runTest {
        audioProviderNotGranted.getAllSongs(context)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_getAllSongByArtist_with_permission_granted() = runTest {
        background.launch {
            with(audioProviderGranted.getAllSongByArtist(context,"artist1")){
                assert(isNotEmpty())
                val count = count()
                assert(count == 2)
                val id1 = find { it.id == 1L }
                val id2 = find { it.id == 2L }
                val id3 = find { it.id == 3L }
                assert(id1 != null)
                assert(id2 != null)
                assert(id3 == null)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = MissingPermissionException::class)
    fun test_getAllSongByArtist_with_permission_not_granted() = runTest {
        audioProviderNotGranted.getAllSongByArtist(context,"artist1")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_getAllSongsByAlbum_with_permission_granted() = runTest {
        with(audioProviderGranted.getAllSongsByAlbum(context,"album2")){
            assert(isNotEmpty())
            val count = count()
            assert(count == 2)
            val id1 = find { it.id == 1L }
            val id2 = find { it.id == 2L }
            val id3 = find { it.id == 3L }
            assert(id1 == null)
            assert(id2 != null)
            assert(id3 != null)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = MissingPermissionException::class)
    fun test_getAllSongsByAlbum_with_permission_NotGranted() = runTest {
        audioProviderNotGranted.getAllSongsByAlbum(context,"album1")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_getAllSongsBySongName_with_permission_granted() = runTest {
        with(audioProviderGranted.getAllSongsBySongName(context,"songName1")){
            assert(isNotEmpty())
            val count = count()
            assert(count == 2)
            val id1 = find { it.id == 1L }
            val id2 = find { it.id == 2L }
            val id3 = find { it.id == 3L }
            assert(id1 != null)
            assert(id2 == null)
            assert(id3 != null)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = MissingPermissionException::class)
    fun test_getAllSongsBySongName_with_permission_not_granted() = runTest {
        audioProviderNotGranted.getAllSongsBySongName(context,"songName1")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_getSongById_with_permission_granted() = runTest {
        with(audioProviderGranted.getSongById(context,1L)){
            assert(this != null)
            val mpAudio = MPAudio(
                id = 1L,
                artistName = "artist1",
                album = "album1",
                songName = "songName1"
            )
            assert(this == mpAudio)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = MissingPermissionException::class)
    fun test_getSongById_with_permission_not_granted() = runTest {
        audioProviderNotGranted.getSongById(context,1L)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_updateIsFavorite_with_permission_not_granted() = runTest {
        with(audioProviderGranted.updateIsFavorite(context,1L,true)){
            assert(this)
        }
    }
}
