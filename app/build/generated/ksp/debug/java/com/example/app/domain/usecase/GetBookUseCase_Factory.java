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
public final class GetBookUseCase_Factory implements Factory<GetBookUseCase> {
  private final Provider<AudioBookRepository> repositoryProvider;

  public GetBookUseCase_Factory(Provider<AudioBookRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetBookUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetBookUseCase_Factory create(Provider<AudioBookRepository> repositoryProvider) {
    return new GetBookUseCase_Factory(repositoryProvider);
  }

  public static GetBookUseCase newInstance(AudioBookRepository repository) {
    return new GetBookUseCase(repository);
  }
}
