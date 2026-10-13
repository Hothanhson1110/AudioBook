package com.example.app.presentation.screens.home;

import com.example.app.domain.usecase.DeleteBookUseCase;
import com.example.app.domain.usecase.ImportDocumentUseCase;
import com.example.app.domain.usecase.ObserveBooksUseCase;
import com.example.app.domain.usecase.SynthesizeBookUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ObserveBooksUseCase> observeBooksProvider;

  private final Provider<ImportDocumentUseCase> importDocumentProvider;

  private final Provider<SynthesizeBookUseCase> synthesizeBookProvider;

  private final Provider<DeleteBookUseCase> deleteBookProvider;

  public HomeViewModel_Factory(Provider<ObserveBooksUseCase> observeBooksProvider,
      Provider<ImportDocumentUseCase> importDocumentProvider,
      Provider<SynthesizeBookUseCase> synthesizeBookProvider,
      Provider<DeleteBookUseCase> deleteBookProvider) {
    this.observeBooksProvider = observeBooksProvider;
    this.importDocumentProvider = importDocumentProvider;
    this.synthesizeBookProvider = synthesizeBookProvider;
    this.deleteBookProvider = deleteBookProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(observeBooksProvider.get(), importDocumentProvider.get(), synthesizeBookProvider.get(), deleteBookProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<ObserveBooksUseCase> observeBooksProvider,
      Provider<ImportDocumentUseCase> importDocumentProvider,
      Provider<SynthesizeBookUseCase> synthesizeBookProvider,
      Provider<DeleteBookUseCase> deleteBookProvider) {
    return new HomeViewModel_Factory(observeBooksProvider, importDocumentProvider, synthesizeBookProvider, deleteBookProvider);
  }

  public static HomeViewModel newInstance(ObserveBooksUseCase observeBooks,
      ImportDocumentUseCase importDocument, SynthesizeBookUseCase synthesizeBook,
      DeleteBookUseCase deleteBook) {
    return new HomeViewModel(observeBooks, importDocument, synthesizeBook, deleteBook);
  }
}
