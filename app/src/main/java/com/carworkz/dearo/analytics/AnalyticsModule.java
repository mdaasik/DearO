package com.carworkz.dearo.analytics;

import android.content.Context;

import com.carworkz.dearo.injection.ApplicationScoped;

import dagger.Module;
import dagger.Provides;

@Module
public class AnalyticsModule {

    @Provides
    @ApplicationScoped
    ScreenTracker providesScreenTracker(Context context) {
        return new FirebaseScreenTracker(context);
    }
}
