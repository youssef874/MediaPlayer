package com.example.mediaplayer3.domain

import com.example.mediaplayer3.repository.IAudioDataRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideAudioSyncUseCase(audioDataRepo: IAudioDataRepo):IAudioSyncUseCase{
        return AudioSyncUseCase(audioDataRepo)
    }

    @Provides
    @Singleton
    fun provideFetUseCase(
        audioDataRepo: IAudioDataRepo
    ): IFetchDataUseCase {
        return FetchDataUseCase(audioDataRepo)
    }

    @Provides
    @Singleton
    fun providePlayAudioUseCase(
        audioDataRepository: IAudioDataRepo,
        fetchDataUseCase: IFetchDataUseCase,
        audioConfiguratorUseCase: IAudioConfiguratorUseCase
    ): IPlayAudioUseCase {
        return PlayAudioUseCase(audioDataRepository, fetchDataUseCase, audioConfiguratorUseCase)
    }

    @Provides
    @Singleton
    fun provideAudioPauseOrResumeUseCase(
        audioDataRepository: IAudioDataRepo,
        playUseCase: IPlayAudioUseCase
    ): IAudioPauseOrResumeUseCase {
        return ResumePauseSongUseCaseImpl(audioDataRepository, playUseCase)
    }

    @Provides
    @Singleton
    fun provideAudioConfiguratorUseCase(
        audioDataRepo: IAudioDataRepo
    ): IAudioConfiguratorUseCase {
        return AudioConfigurationUseCaseImpl(audioDataRepo)
    }

    @Provides
    @Singleton
    fun provideAudioForwardOrRewindUseCase(
        audioDataRepo: IAudioDataRepo,
        playUseCase: IPlayAudioUseCase
    ): IAudioForwardOrRewindUseCase {
        return AudioForwardOrRewindUseCaseImp(audioDataRepo, playUseCase)
    }

    @Provides
    @Singleton
    fun providePlayNextOrPreviousSongUseCase(
        playAudioUseCase: IPlayAudioUseCase,
        fetchDataUseCase: IFetchDataUseCase
    ): IPlayNextOrPreviousSongUseCase{
        return PlayNextPreviousSongUseCase(playAudioUseCase, fetchDataUseCase)
    }

    @Provides
    @Singleton
    fun provideEditSongUseCase(audioDataRepo: IAudioDataRepo): IEditSongUseCase{
        return EditSongUseCase(audioDataRepo)
    }
}