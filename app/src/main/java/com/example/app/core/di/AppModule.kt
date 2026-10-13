package com.example.app.core.di

import com.example.app.data.parser.PdfDocumentParser
import com.example.app.data.parser.WordDocumentParser
import com.example.app.data.repository.AudioBookRepositoryImpl
import com.example.app.domain.repository.AudioBookRepository
import com.example.app.domain.service.DocumentParser
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindRepository(impl: AudioBookRepositoryImpl): AudioBookRepository

    @Binds
    @IntoSet
    abstract fun bindPdfParser(impl: PdfDocumentParser): DocumentParser

    @Binds
    @IntoSet
    abstract fun bindWordParser(impl: WordDocumentParser): DocumentParser
}