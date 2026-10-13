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
public final class PdfDocumentParser_Factory implements Factory<PdfDocumentParser> {
  private final Provider<Context> contextProvider;

  public PdfDocumentParser_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PdfDocumentParser get() {
    return newInstance(contextProvider.get());
  }

  public static PdfDocumentParser_Factory create(Provider<Context> contextProvider) {
    return new PdfDocumentParser_Factory(contextProvider);
  }

  public static PdfDocumentParser newInstance(Context context) {
    return new PdfDocumentParser(context);
  }
}
