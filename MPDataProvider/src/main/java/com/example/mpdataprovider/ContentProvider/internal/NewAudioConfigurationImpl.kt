package com.example.mpdataprovider.ContentProvider.internal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class NewAudioConfigurationImpl: IAudioConfiguration {

    override fun askRequiredPermission(context: Context): List<String> {
        val list = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED){
            list.add(Manifest.permission.READ_MEDIA_AUDIO)
        }
        return list
    }


}