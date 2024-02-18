package com.example.mediaplayer3.service

import kotlinx.coroutines.CoroutineScope

interface IBaseService {

    val scope: CoroutineScope

    fun handleStartServiceRequest()

    fun handleStopServiceRequest()
}