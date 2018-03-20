package geogram.radio.myownradio.di.component;

import javax.inject.Singleton;

import dagger.Component;
import geogram.radio.myownradio.playerpackage.PlayerService;
import geogram.radio.myownradio.adapters.RadioItemsAdapters;
import geogram.radio.myownradio.di.module.ApplicationModule;
import geogram.radio.myownradio.mvp.presenters.PlayerFragmentPresenter;
import geogram.radio.myownradio.ui.fragments.PlayerFragment;

/**
 * Created by geogr on 07.03.2018.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(PlayerFragmentPresenter playerFragmentPresenter);
    void inject(PlayerService playerService);
}
