package com.carworkz.dearo.injection;

import android.content.Context;

import com.carworkz.dearo.notification.NotificationNavigator;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Farhan on 31/7/17.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Context getContext();

    NotificationNavigator getNavigator();
}
