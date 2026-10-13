package com.example.app.data.mapper;

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
public final class AudioBookMapper_Factory implements Factory<AudioBookMapper> {
  @Override
  public AudioBookMapper get() {
    return newInstance();
  }

  public static AudioBookMapper_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AudioBookMapper newInstance() {
    return new AudioBookMapper();
  }

  private static final class InstanceHolder {
    private static final AudioBookMapper_Factory INSTANCE = new AudioBookMapper_Factory();
  }
}
