package com.example.mediaplayer3.viewModel.delegates

import android.content.Context

interface IAudioProgressJob {

    fun launchJob(context: Context)

    fun cancelJob()
}