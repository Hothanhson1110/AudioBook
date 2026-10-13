package com.example.app.playback;

import android.content.Context;
import androidx.media3.session.SessionToken;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class MediaSessionModule_ProvideSessionTokenFactory implements Factory<SessionToken> {
  private final Provider<Context> contextProvider;

  public MediaSessionModule_ProvideSessionTokenFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SessionToken get() {
    return provideSessionToken(contextProvider.get());
  }

  public static MediaSessionModule_ProvideSessionTokenFactory create(
      Provider<Context> contextProvider) {
    return new MediaSessionModule_ProvideSessionTokenFactory(contextProvider);
  }

  public static SessionToken provideSessionToken(Context context) {
    return Preconditions.checkNotNullFromProvides(MediaSessionModule.INSTANCE.provideSessionToken(context));
  }
}
