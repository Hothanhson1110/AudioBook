package com.example.app.core.di;

import com.example.app.data.local.AudioBookDatabase;
import com.example.app.data.local.dao.AudioBookDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideAudioBookDaoFactory implements Factory<AudioBookDao> {
  private final Provider<AudioBookDatabase> dbProvider;

  public DatabaseModule_ProvideAudioBookDaoFactory(Provider<AudioBookDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AudioBookDao get() {
    return provideAudioBookDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideAudioBookDaoFactory create(
      Provider<AudioBookDatabase> dbProvider) {
    return new DatabaseModule_ProvideAudioBookDaoFactory(dbProvider);
  }

  public static AudioBookDao provideAudioBookDao(AudioBookDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAudioBookDao(db));
  }
}
