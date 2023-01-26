package com.example.testyoutube.di

import com.example.testyoutube.data.api.ApiService
import com.example.testyoutube.data.database.dao.VideoDao
import com.example.testyoutube.data.repository.VideoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideVideoRepository(apiService: ApiService, videoDao: VideoDao): VideoRepository {
        return VideoRepository(apiService, videoDao)
    }

}