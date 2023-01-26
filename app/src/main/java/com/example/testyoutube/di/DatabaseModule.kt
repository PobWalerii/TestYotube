package com.example.testyoutube.di

import android.content.Context
import androidx.room.Room
import com.example.testyoutube.data.database.AppDatabase
import com.example.testyoutube.data.database.dao.VideoDao
import com.example.testyoutube.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideClientsDao(database: AppDatabase): VideoDao {
        return database.videoDao()
    }


}