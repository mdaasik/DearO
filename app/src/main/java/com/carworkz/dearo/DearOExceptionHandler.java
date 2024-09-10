package com.carworkz.dearo;

import com.carworkz.dearo.data.local.SharedPrefHelper;
import com.google.firebase.crashlytics.FirebaseCrashlytics;


/*
 * Purpose of this class is to be the middle man when uncaught exception is thrown
 * and log necessary information for debugging purpose.
 * */

public final class DearOExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    private Thread.UncaughtExceptionHandler defaultHandler;

    DearOExceptionHandler() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LoggingFacade.log("Username", SharedPrefHelper.getUserName());
        LoggingFacade.log("WorkshopName", SharedPrefHelper.getWorkShopName());
        if (BuildConfig.DEBUG) {
            LoggingFacade.log("accessToken", SharedPrefHelper.getUserAccessToken());
        }
        LoggingFacade.log(e);
        if (defaultHandler != null)
            defaultHandler.uncaughtException(t, e);
        //log to crashlytics
        FirebaseCrashlytics.getInstance().recordException(e);
    }
}
