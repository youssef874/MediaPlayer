package com.example.mpdataprovider.contentprovider.internal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

internal class OldAudioConfigurationImpl: IAudioConfiguration {
    override fun askRequiredPermission(context: Context): List<String> {
        val list = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED){
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return list
    }
}