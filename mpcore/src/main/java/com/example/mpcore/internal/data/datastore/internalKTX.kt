package com.example.mpcore.internal.data.datastore

import com.example.mpcore.api.data.datastore.data.RepeatMode


internal fun @RepeatMode Int.toDataProviderRepeatMode():@com.example.mpdataprovider.datastore.data.RepeatMode Int =
    when(this){
        RepeatMode.REPEAT_ALL-> com.example.mpdataprovider.datastore.data.RepeatMode.REPEAT_ALL
        RepeatMode.ONE_REPEAT->com.example.mpdataprovider.datastore.data.RepeatMode.ONE_REPEAT
        RepeatMode.NO_REPEAT->com.example.mpdataprovider.datastore.data.RepeatMode.NO_REPEAT
        else-> throw IllegalAccessException("value of $this is not in repeatMode")
    }

internal fun @com.example.mpdataprovider.datastore.data.RepeatMode Int.toThisModuleRepeatMode(): @RepeatMode Int =
    when(this){
        com.example.mpdataprovider.datastore.data.RepeatMode.REPEAT_ALL->RepeatMode.REPEAT_ALL
        com.example.mpdataprovider.datastore.data.RepeatMode.NO_REPEAT->RepeatMode.NO_REPEAT
        com.example.mpdataprovider.datastore.data.RepeatMode.ONE_REPEAT->RepeatMode.ONE_REPEAT
        else-> throw IllegalAccessException("value of $this is not in repeatMode")
    }