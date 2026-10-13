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
public final class MergeChunksWorker_AssistedFactory_Impl implements MergeChunksWorker_AssistedFactory {
  private final MergeChunksWorker_Factory delegateFactory;

  MergeChunksWorker_AssistedFactory_Impl(MergeChunksWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public MergeChunksWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<MergeChunksWorker_AssistedFactory> create(
      MergeChunksWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MergeChunksWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<MergeChunksWorker_AssistedFactory> createFactoryProvider(
      MergeChunksWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MergeChunksWorker_AssistedFactory_Impl(delegateFactory));
  }
}
