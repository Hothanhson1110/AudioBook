package com.example.app.data.worker;

import android.content.Context;
import com.example.app.data.local.dao.AudioBookDao;
import com.example.app.data.parser.DocumentParserFactory;
import com.example.app.domain.service.TextChunker;
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
public final class SynthesisOrchestrator_Factory implements Factory<SynthesisOrchestrator> {
  private final Provider<Context> contextProvider;

  private final Provider<AudioBookDao> daoProvider;

  private final Provider<DocumentParserFactory> parserFactoryProvider;

  private final Provider<TextChunker> chunkerProvider;

  public SynthesisOrchestrator_Factory(Provider<Context> contextProvider,
      Provider<AudioBookDao> daoProvider, Provider<DocumentParserFactory> parserFactoryProvider,
      Provider<TextChunker> chunkerProvider) {
    this.contextProvider = contextProvider;
    this.daoProvider = daoProvider;
    this.parserFactoryProvider = parserFactoryProvider;
    this.chunkerProvider = chunkerProvider;
  }

  @Override
  public SynthesisOrchestrator get() {
    return newInstance(contextProvider.get(), daoProvider.get(), parserFactoryProvider.get(), chunkerProvider.get());
  }

  public static SynthesisOrchestrator_Factory create(Provider<Context> contextProvider,
      Provider<AudioBookDao> daoProvider, Provider<DocumentParserFactory> parserFactoryProvider,
      Provider<TextChunker> chunkerProvider) {
    return new SynthesisOrchestrator_Factory(contextProvider, daoProvider, parserFactoryProvider, chunkerProvider);
  }

  public static SynthesisOrchestrator newInstance(Context context, AudioBookDao dao,
      DocumentParserFactory parserFactory, TextChunker chunker) {
    return new SynthesisOrchestrator(context, dao, parserFactory, chunker);
  }
}
