package com.example.mediaplayer3.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepoModule {


    @Provides
    @Singleton
    fun provideAudioRepo(): IAudioDataRepo{
        return AudioDataRepo()
    }
}