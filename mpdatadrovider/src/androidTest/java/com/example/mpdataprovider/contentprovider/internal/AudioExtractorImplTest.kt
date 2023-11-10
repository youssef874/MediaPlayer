package com.example.mpdataprovider.contentprovider.internal

import android.content.ContentValues
import android.provider.MediaStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioExtractorImplTest{

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val contentResolver = context.contentResolver
    private val audioExtractor = AudioExtractorImpl(contentResolver)

    private val ids = mutableListOf(1L,2L)
    private val albums = mutableListOf("album1","album2")
    private val artists = mutableListOf("artist1","artist2")
    private val durations = mutableListOf(40,60)
    private val sizes = mutableListOf(100,200)
    private val displayNames = mutableListOf("name1","name2")
    private val contentUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

    @Before
    fun setup(){
        val contentValue = ContentValues()
        contentValue.put(MediaStore.Audio.Media._ID,ids.first())
        contentValue.put(MediaStore.Audio.Media.ALBUM,albums.first())
        contentValue.put(MediaStore.Audio.Media.ARTIST,artists.first())
        contentValue.put(MediaStore.Audio.Media.DURATION,durations.first())
        contentValue.put(MediaStore.Audio.Media.SIZE,sizes.first())
        contentValue.put(MediaStore.Audio.Media.DISPLAY_NAME,displayNames.first())
        contentResolver.insert(contentUri,contentValue)
        val secondContentValue = ContentValues()
        contentValue.put(MediaStore.Audio.Media._ID,ids.last())
        contentValue.put(MediaStore.Audio.Media.ALBUM,albums.last())
        contentValue.put(MediaStore.Audio.Media.ARTIST,artists.last())
        contentValue.put(MediaStore.Audio.Media.DURATION,durations.last())
        contentValue.put(MediaStore.Audio.Media.SIZE,sizes.last())
        contentValue.put(MediaStore.Audio.Media.DISPLAY_NAME,displayNames.last())
        contentResolver.insert(contentUri,secondContentValue)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_loadAllAudio_and_getAllAudio() = runTest {
        audioExtractor.loadAllAudio()
        with(audioExtractor.getAllAudio()){
            assert(isNotEmpty())
            val count = count()
            assert(count == 1)
            val existId1 = find { it.id == ids.first() }
            val existId2 = find { it.id == ids.last() }
            assert(existId1 != null)
            assert(existId2 != null)
            val fakeId = find { it.id == 4L }
            assert(fakeId == null)
        }
    }
}