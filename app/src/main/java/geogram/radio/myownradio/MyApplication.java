package geogram.radio.myownradio;

import android.app.Application;

import geogram.radio.myownradio.di.component.ApplicationComponent;
import geogram.radio.myownradio.di.component.DaggerApplicationComponent;
import geogram.radio.myownradio.di.module.ApplicationModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by geogr on 07.03.2018.
 */

public class MyApplication extends Application {
    private static ApplicationComponent sApplicationComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        initComponent();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
    private void initComponent(){
        sApplicationComponent= DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
    }
    public static ApplicationComponent getsApplicationComponent(){
        return sApplicationComponent;
    }
}
