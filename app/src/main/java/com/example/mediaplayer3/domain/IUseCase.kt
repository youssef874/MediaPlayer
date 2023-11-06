package com.example.mediaplayer3.domain

import kotlinx.coroutines.CoroutineScope

interface IUseCase {

    val scope: CoroutineScope
}