package com.example.mpdataprovider.internal

import com.example.mpdataprovider.ContentProvider.data.MPAudio
import com.example.mpdataprovider.ContentProvider.internal.IAudioExtractor

class FakeAudioExtractor: IAudioExtractor {

    private val cache = mutableListOf<MPAudio>()

    override suspend fun loadAllAudio() {
        cache.add(
            MPAudio(
                id = 1L,
                artistName = "artist1",
                album = "album1",
                songName = "songName1"
            )
        )
        cache.add(
            MPAudio(
                id = 2L,
                artistName = "artist1",
                album = "album2",
                songName = "songName2"
            )
        )
        cache.add(
            MPAudio(
                id = 3L,
                artistName = "artist2",
                album = "album2",
                songName = "songName1"
            )
        )
    }

    override fun getAllAudio(): List<MPAudio> {
        return cache
    }
}