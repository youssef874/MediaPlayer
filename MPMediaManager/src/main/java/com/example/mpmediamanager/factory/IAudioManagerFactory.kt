package com.example.mpmediamanager.factory

import com.example.mpmediamanager.internal.IAudioPlayerManager

internal interface IAudioManagerFactory {

    fun create(): IAudioPlayerManager
}