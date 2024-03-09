package com.example.mpdataprovider.datastore.data

import androidx.annotation.IntDef

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION
)
@Retention(AnnotationRetention.SOURCE)
@IntDef(
    RepeatMode.NO_REPEAT,
    RepeatMode.ONE_REPEAT,
    RepeatMode.REPEAT_ALL
)
annotation class RepeatMode{
    companion object{
        const val NO_REPEAT = 0
        const val ONE_REPEAT = 1
        const val REPEAT_ALL = 2
    }
}