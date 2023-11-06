package com.example.mediaplayer3.domain

interface IUseCaseJobScheduler {

    fun launchJob(vararg any: Any)

    fun cancelJob()
}