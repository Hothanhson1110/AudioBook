package com.example.app.domain.usecase;

import com.example.app.domain.repository.AudioBookRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class SynthesizeBookUseCase_Factory implements Factory<SynthesizeBookUseCase> {
  private final Provider<AudioBookRepository> repositoryProvider;

  public SynthesizeBookUseCase_Factory(Provider<AudioBookRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SynthesizeBookUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SynthesizeBookUseCase_Factory create(
      Provider<AudioBookRepository> repositoryProvider) {
    return new SynthesizeBookUseCase_Factory(repositoryProvider);
  }

  public static SynthesizeBookUseCase newInstance(AudioBookRepository repository) {
    return new SynthesizeBookUseCase(repository);
  }
}
