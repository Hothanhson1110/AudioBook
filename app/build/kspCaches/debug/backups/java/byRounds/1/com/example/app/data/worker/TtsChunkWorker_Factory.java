package com.example.app.data.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class TtsChunkWorker_Factory {
  public TtsChunkWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params);
  }

  public static TtsChunkWorker_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TtsChunkWorker newInstance(Context context, WorkerParameters params) {
    return new TtsChunkWorker(context, params);
  }

  private static final class InstanceHolder {
    private static final TtsChunkWorker_Factory INSTANCE = new TtsChunkWorker_Factory();
  }
}
