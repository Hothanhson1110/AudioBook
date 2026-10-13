package com.example.app.domain.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class TextChunker_Factory implements Factory<TextChunker> {
  @Override
  public TextChunker get() {
    return newInstance();
  }

  public static TextChunker_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TextChunker newInstance() {
    return new TextChunker();
  }

  private static final class InstanceHolder {
    private static final TextChunker_Factory INSTANCE = new TextChunker_Factory();
  }
}
