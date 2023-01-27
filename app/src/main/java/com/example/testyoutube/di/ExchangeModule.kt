package com.example.testyoutube.di

import com.example.testyoutube.data.exchange.VideoExchange
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExchangeModule {

    @Singleton
    @Provides
    fun provideVideoExchange(): VideoExchange {
        return VideoExchange()
    }

}