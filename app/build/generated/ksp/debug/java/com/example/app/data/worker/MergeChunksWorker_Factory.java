package com.example.app.data.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.example.app.data.local.dao.AudioBookDao;
import dagger.internal.DaggerGenerated;
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
public final class MergeChunksWorker_Factory {
  private final Provider<AudioBookDao> daoProvider;

  public MergeChunksWorker_Factory(Provider<AudioBookDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  public MergeChunksWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, daoProvider.get());
  }

  public static MergeChunksWorker_Factory create(Provider<AudioBookDao> daoProvider) {
    return new MergeChunksWorker_Factory(daoProvider);
  }

  public static MergeChunksWorker newInstance(Context context, WorkerParameters params,
      AudioBookDao dao) {
    return new MergeChunksWorker(context, params, dao);
  }
}
