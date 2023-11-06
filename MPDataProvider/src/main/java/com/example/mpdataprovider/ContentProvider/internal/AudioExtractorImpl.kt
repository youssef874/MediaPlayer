package com.example.mpdataprovider.ContentProvider.internal

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.mpdataprovider.ContentProvider.data.MPAudio
import com.example.mpdataprovider.ContentProvider.data.UpdateException
import com.example.mpdataprovider.getAlbumThumbnailPath
import com.example.mplog.MPLogger

internal class AudioExtractorImpl(private val contentResolver: ContentResolver): IAudioExtractor {

    private val allSongs = mutableListOf<MPAudio>()

    private var isCached = false

    override suspend fun loadAllAudio() {
        if (isCached) {
            MPLogger.w(CLASS_NAME,"loadAllAudio", TAG,"all audio already loaded so no loading is needed")
            return
        }
        MPLogger.i(CLASS_NAME,"loadAllAudio", TAG,"Start loading all audio in content resolver")
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val projections = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.IS_FAVORITE
        )


        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val query = contentResolver.query(
            collection,
            projections,
            selection,
            null,
            null
        )
        query?.let { cursor ->
            val idIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val albumIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val displayNameIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val albumIdIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val isFavoriteIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_FAVORITE)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val album = cursor.getString(albumIndex)
                val artist = cursor.getString(artistIndex)
                val duration = cursor.getInt(durationIndex)
                val size = cursor.getInt(sizeIndex)
                val isFavorite = cursor.getInt(isFavoriteIndex) == 1
                val displayName = cursor.getString(displayNameIndex)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val albumId = cursor.getLong(albumIdIndex)
                val albumCover = getAlbumThumbnailUri(albumId)
                val mpAudio = MPAudio(
                    id = id,
                    uri = contentUri, duration = duration, size = size,
                    artistName = artist, album = album, songName = displayName,
                    albumThumbnailUri = albumCover, isFavorite = isFavorite
                )
                allSongs += mpAudio
            }
        }
        query?.close()
        MPLogger.i(CLASS_NAME,"loadAllAudio", TAG,"complete Loading all audio in content resolver")
    }

    private fun getAlbumThumbnailUri(albumId: Long): Uri? {
        return Uri.parse(getAlbumThumbnailPath(albumId))
    }

    override fun getAllAudio(): List<MPAudio> {
        MPLogger.i(CLASS_NAME,"getAllAudio", TAG,"all songs size: ${allSongs.size}")
        return allSongs
    }

    private companion object{
        private const val CLASS_NAME = "AudioExtractorImpl"
        private const val TAG = "AUDIO_PROVIDER"
    }

}
