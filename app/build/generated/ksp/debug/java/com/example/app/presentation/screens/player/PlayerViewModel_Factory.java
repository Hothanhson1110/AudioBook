package com.example.app.presentation.screens.player;

import android.content.Context;
import androidx.lifecycle.SavedStateHandle;
import androidx.media3.session.SessionToken;
import com.example.app.domain.usecase.GetBookUseCase;
import com.example.app.domain.usecase.UpdatePlaybackUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<GetBookUseCase> getBookProvider;

  private final Provider<UpdatePlaybackUseCase> updatePlaybackProvider;

  private final Provider<SessionToken> sessionTokenProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public PlayerViewModel_Factory(Provider<Context> contextProvider,
      Provider<GetBookUseCase> getBookProvider,
      Provider<UpdatePlaybackUseCase> updatePlaybackProvider,
      Provider<SessionToken> sessionTokenProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.contextProvider = contextProvider;
    this.getBookProvider = getBookProvider;
    this.updatePlaybackProvider = updatePlaybackProvider;
    this.sessionTokenProvider = sessionTokenProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(contextProvider.get(), getBookProvider.get(), updatePlaybackProvider.get(), sessionTokenProvider.get(), savedStateHandleProvider.get());
  }

  public static PlayerViewModel_Factory create(Provider<Context> contextProvider,
      Provider<GetBookUseCase> getBookProvider,
      Provider<UpdatePlaybackUseCase> updatePlaybackProvider,
      Provider<SessionToken> sessionTokenProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new PlayerViewModel_Factory(contextProvider, getBookProvider, updatePlaybackProvider, sessionTokenProvider, savedStateHandleProvider);
  }

  public static PlayerViewModel newInstance(Context context, GetBookUseCase getBook,
      UpdatePlaybackUseCase updatePlayback, SessionToken sessionToken,
      SavedStateHandle savedStateHandle) {
    return new PlayerViewModel(context, getBook, updatePlayback, sessionToken, savedStateHandle);
  }
}
