package com.example.mediaplayer3.viewModel

import com.example.mediaplayer3.repository.AudioDataRepo
import com.example.mediaplayer3.repository.IAudioDataRepo

fun getAudioDataRepo(): IAudioDataRepo = AudioDataRepo()