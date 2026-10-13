package com.example.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.media3.session.SessionToken;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.example.app.core.di.DatabaseModule_ProvideAudioBookDaoFactory;
import com.example.app.core.di.DatabaseModule_ProvideChapterDaoFactory;
import com.example.app.core.di.DatabaseModule_ProvideDatabaseFactory;
import com.example.app.core.di.NetworkModule_ProvideApiFactory;
import com.example.app.core.di.NetworkModule_ProvideOkHttpFactory;
import com.example.app.core.di.NetworkModule_ProvideRetrofitFactory;
import com.example.app.data.local.AudioBookDatabase;
import com.example.app.data.local.dao.AudioBookDao;
import com.example.app.data.local.dao.ChapterDao;
import com.example.app.data.mapper.AudioBookMapper;
import com.example.app.data.parser.DocumentParserFactory;
import com.example.app.data.parser.PdfDocumentParser;
import com.example.app.data.parser.WordDocumentParser;
import com.example.app.data.remote.ApiService;
import com.example.app.data.repository.AudioBookRepositoryImpl;
import com.example.app.data.worker.MergeChunksWorker;
import com.example.app.data.worker.MergeChunksWorker_AssistedFactory;
import com.example.app.data.worker.SynthesisOrchestrator;
import com.example.app.data.worker.TtsChunkWorker;
import com.example.app.data.worker.TtsChunkWorker_AssistedFactory;
import com.example.app.domain.service.DocumentParser;
import com.example.app.domain.service.TextChunker;
import com.example.app.domain.usecase.DeleteBookUseCase;
import com.example.app.domain.usecase.GetBookUseCase;
import com.example.app.domain.usecase.ImportDocumentUseCase;
import com.example.app.domain.usecase.ObserveBooksUseCase;
import com.example.app.domain.usecase.SynthesizeBookUseCase;
import com.example.app.domain.usecase.UpdatePlaybackUseCase;
import com.example.app.playback.MediaSessionModule_ProvideSessionTokenFactory;
import com.example.app.playback.PlaybackService;
import com.example.app.presentation.screens.home.HomeViewModel;
import com.example.app.presentation.screens.home.HomeViewModel_HiltModules;
import com.example.app.presentation.screens.home.HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.app.presentation.screens.home.HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.app.presentation.screens.library.LibraryViewModel;
import com.example.app.presentation.screens.library.LibraryViewModel_HiltModules;
import com.example.app.presentation.screens.library.LibraryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.app.presentation.screens.library.LibraryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.app.presentation.screens.player.PlayerViewModel;
import com.example.app.presentation.screens.player.PlayerViewModel_HiltModules;
import com.example.app.presentation.screens.player.PlayerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.app.presentation.screens.player.PlayerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class DaggerAudioBookApplication_HiltComponents_SingletonC {
  private DaggerAudioBookApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public AudioBookApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements AudioBookApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public AudioBookApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements AudioBookApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public AudioBookApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements AudioBookApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public AudioBookApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements AudioBookApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AudioBookApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements AudioBookApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AudioBookApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements AudioBookApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public AudioBookApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements AudioBookApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public AudioBookApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends AudioBookApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends AudioBookApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends AudioBookApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends AudioBookApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>of(HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HomeViewModel_HiltModules.KeyModule.provide(), LibraryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LibraryViewModel_HiltModules.KeyModule.provide(), PlayerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PlayerViewModel_HiltModules.KeyModule.provide()));
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends AudioBookApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LibraryViewModel> libraryViewModelProvider;

    private Provider<PlayerViewModel> playerViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private ObserveBooksUseCase observeBooksUseCase() {
      return new ObserveBooksUseCase(singletonCImpl.audioBookRepositoryImplProvider.get());
    }

    private ImportDocumentUseCase importDocumentUseCase() {
      return new ImportDocumentUseCase(singletonCImpl.audioBookRepositoryImplProvider.get());
    }

    private SynthesizeBookUseCase synthesizeBookUseCase() {
      return new SynthesizeBookUseCase(singletonCImpl.audioBookRepositoryImplProvider.get());
    }

    private DeleteBookUseCase deleteBookUseCase() {
      return new DeleteBookUseCase(singletonCImpl.audioBookRepositoryImplProvider.get());
    }

    private GetBookUseCase getBookUseCase() {
      return new GetBookUseCase(singletonCImpl.audioBookRepositoryImplProvider.get());
    }

    private UpdatePlaybackUseCase updatePlaybackUseCase() {
      return new UpdatePlaybackUseCase(singletonCImpl.audioBookRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.libraryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.playerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>of(HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) homeViewModelProvider), LibraryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) libraryViewModelProvider), PlayerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) playerViewModelProvider)));
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.example.app.presentation.screens.home.HomeViewModel 
          return (T) new HomeViewModel(viewModelCImpl.observeBooksUseCase(), viewModelCImpl.importDocumentUseCase(), viewModelCImpl.synthesizeBookUseCase(), viewModelCImpl.deleteBookUseCase());

          case 1: // com.example.app.presentation.screens.library.LibraryViewModel 
          return (T) new LibraryViewModel(viewModelCImpl.observeBooksUseCase());

          case 2: // com.example.app.presentation.screens.player.PlayerViewModel 
          return (T) new PlayerViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), viewModelCImpl.getBookUseCase(), viewModelCImpl.updatePlaybackUseCase(), singletonCImpl.provideSessionTokenProvider.get(), viewModelCImpl.savedStateHandle);

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends AudioBookApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends AudioBookApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectPlaybackService(PlaybackService playbackService) {
    }
  }

  private static final class SingletonCImpl extends AudioBookApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AudioBookDatabase> provideDatabaseProvider;

    private Provider<MergeChunksWorker_AssistedFactory> mergeChunksWorker_AssistedFactoryProvider;

    private Provider<TtsChunkWorker_AssistedFactory> ttsChunkWorker_AssistedFactoryProvider;

    private Provider<OkHttpClient> provideOkHttpProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<ApiService> provideApiProvider;

    private Provider<PdfDocumentParser> pdfDocumentParserProvider;

    private Provider<WordDocumentParser> wordDocumentParserProvider;

    private Provider<DocumentParserFactory> documentParserFactoryProvider;

    private Provider<AudioBookMapper> audioBookMapperProvider;

    private Provider<TextChunker> textChunkerProvider;

    private Provider<SynthesisOrchestrator> synthesisOrchestratorProvider;

    private Provider<AudioBookRepositoryImpl> audioBookRepositoryImplProvider;

    private Provider<SessionToken> provideSessionTokenProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private AudioBookDao audioBookDao() {
      return DatabaseModule_ProvideAudioBookDaoFactory.provideAudioBookDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return ImmutableMap.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>of("com.example.app.data.worker.MergeChunksWorker", ((Provider) mergeChunksWorker_AssistedFactoryProvider), "com.example.app.data.worker.TtsChunkWorker", ((Provider) ttsChunkWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private ChapterDao chapterDao() {
      return DatabaseModule_ProvideChapterDaoFactory.provideChapterDao(provideDatabaseProvider.get());
    }

    private Set<DocumentParser> setOfDocumentParser() {
      return ImmutableSet.<DocumentParser>of(pdfDocumentParserProvider.get(), wordDocumentParserProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AudioBookDatabase>(singletonCImpl, 1));
      this.mergeChunksWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<MergeChunksWorker_AssistedFactory>(singletonCImpl, 0));
      this.ttsChunkWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<TtsChunkWorker_AssistedFactory>(singletonCImpl, 2));
      this.provideOkHttpProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 6));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 5));
      this.provideApiProvider = DoubleCheck.provider(new SwitchingProvider<ApiService>(singletonCImpl, 4));
      this.pdfDocumentParserProvider = DoubleCheck.provider(new SwitchingProvider<PdfDocumentParser>(singletonCImpl, 8));
      this.wordDocumentParserProvider = DoubleCheck.provider(new SwitchingProvider<WordDocumentParser>(singletonCImpl, 9));
      this.documentParserFactoryProvider = DoubleCheck.provider(new SwitchingProvider<DocumentParserFactory>(singletonCImpl, 7));
      this.audioBookMapperProvider = DoubleCheck.provider(new SwitchingProvider<AudioBookMapper>(singletonCImpl, 10));
      this.textChunkerProvider = DoubleCheck.provider(new SwitchingProvider<TextChunker>(singletonCImpl, 12));
      this.synthesisOrchestratorProvider = DoubleCheck.provider(new SwitchingProvider<SynthesisOrchestrator>(singletonCImpl, 11));
      this.audioBookRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AudioBookRepositoryImpl>(singletonCImpl, 3));
      this.provideSessionTokenProvider = DoubleCheck.provider(new SwitchingProvider<SessionToken>(singletonCImpl, 13));
    }

    @Override
    public void injectAudioBookApplication(AudioBookApplication audioBookApplication) {
      injectAudioBookApplication2(audioBookApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private AudioBookApplication injectAudioBookApplication2(AudioBookApplication instance) {
      AudioBookApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.example.app.data.worker.MergeChunksWorker_AssistedFactory 
          return (T) new MergeChunksWorker_AssistedFactory() {
            @Override
            public MergeChunksWorker create(Context context, WorkerParameters params) {
              return new MergeChunksWorker(context, params, singletonCImpl.audioBookDao());
            }
          };

          case 1: // com.example.app.data.local.AudioBookDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.example.app.data.worker.TtsChunkWorker_AssistedFactory 
          return (T) new TtsChunkWorker_AssistedFactory() {
            @Override
            public TtsChunkWorker create(Context context2, WorkerParameters params2) {
              return new TtsChunkWorker(context2, params2);
            }
          };

          case 3: // com.example.app.data.repository.AudioBookRepositoryImpl 
          return (T) new AudioBookRepositoryImpl(singletonCImpl.audioBookDao(), singletonCImpl.chapterDao(), singletonCImpl.provideApiProvider.get(), singletonCImpl.documentParserFactoryProvider.get(), singletonCImpl.audioBookMapperProvider.get(), singletonCImpl.synthesisOrchestratorProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.example.app.data.remote.ApiService 
          return (T) NetworkModule_ProvideApiFactory.provideApi(singletonCImpl.provideRetrofitProvider.get());

          case 5: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpProvider.get());

          case 6: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpFactory.provideOkHttp();

          case 7: // com.example.app.data.parser.DocumentParserFactory 
          return (T) new DocumentParserFactory(singletonCImpl.setOfDocumentParser());

          case 8: // com.example.app.data.parser.PdfDocumentParser 
          return (T) new PdfDocumentParser(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.example.app.data.parser.WordDocumentParser 
          return (T) new WordDocumentParser(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.example.app.data.mapper.AudioBookMapper 
          return (T) new AudioBookMapper();

          case 11: // com.example.app.data.worker.SynthesisOrchestrator 
          return (T) new SynthesisOrchestrator(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.audioBookDao(), singletonCImpl.documentParserFactoryProvider.get(), singletonCImpl.textChunkerProvider.get());

          case 12: // com.example.app.domain.service.TextChunker 
          return (T) new TextChunker();

          case 13: // androidx.media3.session.SessionToken 
          return (T) MediaSessionModule_ProvideSessionTokenFactory.provideSessionToken(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
