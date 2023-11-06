package com.example.mpdataprovider.ContentProvider.internal

import android.content.Context

/**
 * A setup required to do before searching for the all songs in device
 */
internal interface IAudioConfiguration {

    /**
     * check for the needed permission
     * @param context: Android context
     * @return list of not granted permission which mean if the returned list is empty then all permission is granted
     * */
    fun askRequiredPermission(context: Context):List<String>
}