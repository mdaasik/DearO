package com.carworkz.dearo.injection;

import android.content.Context;

import com.carworkz.dearo.notification.NotificationNavigator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ambab on 27/7/17.
 */

@Module
public final class ApplicationModule {

    private final Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    Context providesApplicationContext() {
        return context;
    }

    @Provides
    @Singleton
    NotificationNavigator providesNavigator() {
        return new NotificationNavigator(context);
    }


}
