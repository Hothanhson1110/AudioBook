package com.example.app.core.di;

import com.example.app.data.local.AudioBookDatabase;
import com.example.app.data.local.dao.ChapterDao;
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
public final class DatabaseModule_ProvideChapterDaoFactory implements Factory<ChapterDao> {
  private final Provider<AudioBookDatabase> dbProvider;

  public DatabaseModule_ProvideChapterDaoFactory(Provider<AudioBookDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ChapterDao get() {
    return provideChapterDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideChapterDaoFactory create(
      Provider<AudioBookDatabase> dbProvider) {
    return new DatabaseModule_ProvideChapterDaoFactory(dbProvider);
  }

  public static ChapterDao provideChapterDao(AudioBookDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideChapterDao(db));
  }
}
