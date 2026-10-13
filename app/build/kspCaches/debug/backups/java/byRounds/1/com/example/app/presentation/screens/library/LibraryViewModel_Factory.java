package com.example.app.presentation.screens.library;

import com.example.app.domain.usecase.ObserveBooksUseCase;
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
public final class LibraryViewModel_Factory implements Factory<LibraryViewModel> {
  private final Provider<ObserveBooksUseCase> observeBooksProvider;

  public LibraryViewModel_Factory(Provider<ObserveBooksUseCase> observeBooksProvider) {
    this.observeBooksProvider = observeBooksProvider;
  }

  @Override
  public LibraryViewModel get() {
    return newInstance(observeBooksProvider.get());
  }

  public static LibraryViewModel_Factory create(
      Provider<ObserveBooksUseCase> observeBooksProvider) {
    return new LibraryViewModel_Factory(observeBooksProvider);
  }

  public static LibraryViewModel newInstance(ObserveBooksUseCase observeBooks) {
    return new LibraryViewModel(observeBooks);
  }
}
