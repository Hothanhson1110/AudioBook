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
public final class ImportDocumentUseCase_Factory implements Factory<ImportDocumentUseCase> {
  private final Provider<AudioBookRepository> repositoryProvider;

  public ImportDocumentUseCase_Factory(Provider<AudioBookRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ImportDocumentUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ImportDocumentUseCase_Factory create(
      Provider<AudioBookRepository> repositoryProvider) {
    return new ImportDocumentUseCase_Factory(repositoryProvider);
  }

  public static ImportDocumentUseCase newInstance(AudioBookRepository repository) {
    return new ImportDocumentUseCase(repository);
  }
}
