package com.example.mediaplayer3.viewModel

import com.example.mediaplayer3.repository.AudioDataRepo
import com.example.mediaplayer3.repository.AudioRepositoryImpl
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mediaplayer3.repository.IAudioRepository
import kotlinx.coroutines.CoroutineScope

fun getAudioRepo(coroutineScope: CoroutineScope): IAudioRepository = AudioRepositoryImpl(coroutineScope)

fun getAudioDataRepo(): IAudioDataRepo = AudioDataRepo()