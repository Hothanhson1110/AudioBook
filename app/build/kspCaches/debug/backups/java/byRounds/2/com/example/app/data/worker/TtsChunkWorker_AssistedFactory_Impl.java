package com.example.app.data.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class TtsChunkWorker_AssistedFactory_Impl implements TtsChunkWorker_AssistedFactory {
  private final TtsChunkWorker_Factory delegateFactory;

  TtsChunkWorker_AssistedFactory_Impl(TtsChunkWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public TtsChunkWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<TtsChunkWorker_AssistedFactory> create(
      TtsChunkWorker_Factory delegateFactory) {
    return InstanceFactory.create(new TtsChunkWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<TtsChunkWorker_AssistedFactory> createFactoryProvider(
      TtsChunkWorker_Factory delegateFactory) {
    return InstanceFactory.create(new TtsChunkWorker_AssistedFactory_Impl(delegateFactory));
  }
}
