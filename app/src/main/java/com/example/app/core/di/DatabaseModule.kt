package com.example.app.core.di

import android.content.Context
import androidx.room.Room
import com.example.app.data.local.AudioBookDatabase
import com.example.app.data.local.dao.AudioBookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AudioBookDatabase =
        Room.databaseBuilder(ctx, AudioBookDatabase::class.java, "audiobook.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDao(db: AudioBookDatabase): AudioBookDao = db.audioBookDao()
}