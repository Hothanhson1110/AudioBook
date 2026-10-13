package com.example.app;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AudioBookApplication_MembersInjector implements MembersInjector<AudioBookApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public AudioBookApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<AudioBookApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new AudioBookApplication_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(AudioBookApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.example.app.AudioBookApplication.workerFactory")
  public static void injectWorkerFactory(AudioBookApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
