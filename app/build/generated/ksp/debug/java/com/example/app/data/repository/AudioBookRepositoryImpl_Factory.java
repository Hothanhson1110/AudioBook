package com.example.app.data.repository;

import android.content.Context;
import com.example.app.data.local.dao.AudioBookDao;
import com.example.app.data.local.dao.ChapterDao;
import com.example.app.data.mapper.AudioBookMapper;
import com.example.app.data.parser.DocumentParserFactory;
import com.example.app.data.remote.ApiService;
import com.example.app.data.worker.SynthesisOrchestrator;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AudioBookRepositoryImpl_Factory implements Factory<AudioBookRepositoryImpl> {
  private final Provider<AudioBookDao> daoProvider;

  private final Provider<ChapterDao> chapterDaoProvider;

  private final Provider<ApiService> apiProvider;

  private final Provider<DocumentParserFactory> parserFactoryProvider;

  private final Provider<AudioBookMapper> mapperProvider;

  private final Provider<SynthesisOrchestrator> orchestratorProvider;

  private final Provider<Context> contextProvider;

  public AudioBookRepositoryImpl_Factory(Provider<AudioBookDao> daoProvider,
      Provider<ChapterDao> chapterDaoProvider, Provider<ApiService> apiProvider,
      Provider<DocumentParserFactory> parserFactoryProvider,
      Provider<AudioBookMapper> mapperProvider,
      Provider<SynthesisOrchestrator> orchestratorProvider, Provider<Context> contextProvider) {
    this.daoProvider = daoProvider;
    this.chapterDaoProvider = chapterDaoProvider;
    this.apiProvider = apiProvider;
    this.parserFactoryProvider = parserFactoryProvider;
    this.mapperProvider = mapperProvider;
    this.orchestratorProvider = orchestratorProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public AudioBookRepositoryImpl get() {
    return newInstance(daoProvider.get(), chapterDaoProvider.get(), apiProvider.get(), parserFactoryProvider.get(), mapperProvider.get(), orchestratorProvider.get(), contextProvider.get());
  }

  public static AudioBookRepositoryImpl_Factory create(Provider<AudioBookDao> daoProvider,
      Provider<ChapterDao> chapterDaoProvider, Provider<ApiService> apiProvider,
      Provider<DocumentParserFactory> parserFactoryProvider,
      Provider<AudioBookMapper> mapperProvider,
      Provider<SynthesisOrchestrator> orchestratorProvider, Provider<Context> contextProvider) {
    return new AudioBookRepositoryImpl_Factory(daoProvider, chapterDaoProvider, apiProvider, parserFactoryProvider, mapperProvider, orchestratorProvider, contextProvider);
  }

  public static AudioBookRepositoryImpl newInstance(AudioBookDao dao, ChapterDao chapterDao,
      ApiService api, DocumentParserFactory parserFactory, AudioBookMapper mapper,
      SynthesisOrchestrator orchestrator, Context context) {
    return new AudioBookRepositoryImpl(dao, chapterDao, api, parserFactory, mapper, orchestrator, context);
  }
}
