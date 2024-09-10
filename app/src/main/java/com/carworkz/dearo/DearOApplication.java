package com.carworkz.dearo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;
import com.carworkz.dearo.base.EventsManager;
import com.carworkz.dearo.data.DaggerDataRepositoryComponent;
import com.carworkz.dearo.data.DataRepositoryComponent;
import com.carworkz.dearo.data.local.SharedPrefHelper;
import com.carworkz.dearo.events.AccessTokenExpiredEvent;
import com.carworkz.dearo.injection.ApplicationComponent;
import com.carworkz.dearo.injection.ApplicationModule;
import com.carworkz.dearo.injection.DaggerApplicationComponent;
import com.carworkz.dearo.login.addmobilenumber.LoginActivity;
import com.carworkz.library.downloader.PRDownloader;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.greenrobot.eventbus.Subscribe;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by Farhan on 25/7/17.
 */

public class DearOApplication extends Application implements EventsManager.EventSubscriber {

    private static final String TAG = "DearOApplication";
    @SuppressLint("StaticFieldLeak")
    public static Context sContext;
    private ApplicationComponent component;
    private DataRepositoryComponent repositoryComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        EventsManager.register(this);
        LoggingFacade.enableLogging();

        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration
                .Builder()
                .name("DearO.realm")
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(configuration);

        component = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        repositoryComponent = DaggerDataRepositoryComponent
                .builder()
                .applicationComponent(getApplicationComponent())
                .build();

        PRDownloader.initialize(getApplicationContext());

        Thread.setDefaultUncaughtExceptionHandler(new DearOExceptionHandler());

        //Firebase config
        if(!SharedPrefHelper.getUserAccessToken().isEmpty()) {
            FirebaseCrashlytics.getInstance().setUserId(SharedPrefHelper.getUserId());
            FirebaseCrashlytics.getInstance().setCustomKey("User Name", SharedPrefHelper.getUserName());
            FirebaseCrashlytics.getInstance().setCustomKey("Workshop Name", SharedPrefHelper.getWorkShopName());
            FirebaseCrashlytics.getInstance().setCustomKey("Workshop Id", SharedPrefHelper.getWorkshopId());
            FirebaseCrashlytics.getInstance().setCustomKey("session", SharedPrefHelper.getUserAccessToken());
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    public ApplicationComponent getApplicationComponent() {
        return component;
    }

    public DataRepositoryComponent getRepositoryComponent() {
        return repositoryComponent;
    }


    @Subscribe
    public void onAccessTokenExpired(AccessTokenExpiredEvent event) {
        SharedPrefHelper.logout();
        startActivity(LoginActivity.getStartIntent(this, getString(R.string.token_expiration)));
    }
}
