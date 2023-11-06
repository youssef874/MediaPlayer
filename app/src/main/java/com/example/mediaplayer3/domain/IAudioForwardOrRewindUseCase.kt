package com.example.mediaplayer3.domain

import android.content.Context
import android.net.Uri
import com.example.mediaplayer3.ui.Constant

interface IAudioForwardOrRewindUseCase {

    fun forward(forwardAt: Int = Constant.Utils.DELTA_TIME)

    fun rewind(rewindAt: Int = Constant.Utils.DELTA_TIME)

    fun setPlayingPosition(context: Context, uri: Uri, position: Int)
}