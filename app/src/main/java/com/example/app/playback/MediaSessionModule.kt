package com.example.app.playback

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaSessionModule {

    @Provides
    @Singleton
    fun provideSessionToken(@ApplicationContext context: Context): SessionToken =
        SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java),
        )
}