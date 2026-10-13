package com.example.app.data.parser;

import android.content.Context;
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
public final class WordDocumentParser_Factory implements Factory<WordDocumentParser> {
  private final Provider<Context> contextProvider;

  public WordDocumentParser_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WordDocumentParser get() {
    return newInstance(contextProvider.get());
  }

  public static WordDocumentParser_Factory create(Provider<Context> contextProvider) {
    return new WordDocumentParser_Factory(contextProvider);
  }

  public static WordDocumentParser newInstance(Context context) {
    return new WordDocumentParser(context);
  }
}
