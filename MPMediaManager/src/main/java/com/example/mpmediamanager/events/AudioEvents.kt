package com.example.mpmediamanager.events

import androidx.annotation.StringDef
import com.example.mpeventhandler.data.MPEvent


@StringDef(
    SongChangesEventType.SONG_COMPLETED,
    SongChangesEventType.SONG_PROGRESS_CHANGES
)

annotation class SongChangesEventType{
    companion object{
        const val SONG_COMPLETED = "song_completed"
        const val SONG_PROGRESS_CHANGES = "song_progress_changes"
    }
}

data object SongCompletedEvent: MPEvent(SongChangesEventType.SONG_COMPLETED)

data class SongProgressChanges(val progress: Int):MPEvent(SongChangesEventType.SONG_PROGRESS_CHANGES)