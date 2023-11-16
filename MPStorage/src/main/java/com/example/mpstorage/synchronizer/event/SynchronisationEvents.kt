package com.example.mpstorage.synchronizer.event

import androidx.annotation.StringDef
import com.example.mpeventhandler.data.MPEvent

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION
)
@Retention(AnnotationRetention.SOURCE)
@StringDef(
    SynchronisationType.SYNCHRONISATION_STARTED,
    SynchronisationType.SYNCHRONISATION_COMPLETED,
    SynchronisationType.SYNCHRONIZATION_FAILED
)

annotation class SynchronisationType{

    companion object{
        const val SYNCHRONISATION_STARTED = "synchronisation_started"
        const val SYNCHRONISATION_COMPLETED = "synchronisation_completed"
        const val SYNCHRONIZATION_FAILED = "synchronisation_failed"
    }
}

data class SynchronisationChanges(@SynchronisationType val synchronizationType: String): MPEvent(synchronizationType)