package com.example.app.data.parser;

import com.example.app.domain.service.DocumentParser;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Set;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DocumentParserFactory_Factory implements Factory<DocumentParserFactory> {
  private final Provider<Set<DocumentParser>> parsersProvider;

  public DocumentParserFactory_Factory(Provider<Set<DocumentParser>> parsersProvider) {
    this.parsersProvider = parsersProvider;
  }

  @Override
  public DocumentParserFactory get() {
    return newInstance(parsersProvider.get());
  }

  public static DocumentParserFactory_Factory create(
      Provider<Set<DocumentParser>> parsersProvider) {
    return new DocumentParserFactory_Factory(parsersProvider);
  }

  public static DocumentParserFactory newInstance(Set<DocumentParser> parsers) {
    return new DocumentParserFactory(parsers);
  }
}
