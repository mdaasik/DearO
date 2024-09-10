package com.carworkz.dearo;

import com.carworkz.library.downloader.PRDownloader;
import com.codemonkeylabs.fpslibrary.TinyDancer;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

/**
 * Created by Farhan on 25/7/17.
 */

public class DearODebugApplication extends DearOApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
       // StrictMode.enableDefaults();

        if (BuildConfig.ENABLE_STETHO) {
            Stetho.initializeWithDefaults(this);
        }

        if (BuildConfig.ENABLE_LEAK_CANARY) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
        }

        if (BuildConfig.ENABLE_TINY_DANCER) {
            TinyDancer.create()
                    .show(this);
        }
        PRDownloader.initialize(getApplicationContext());
    }
}
