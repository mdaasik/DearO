package com.carworkz.dearo;

//import com.crashlytics.android.Crashlytics;

//import io.fabric.sdk.android.Fabric;

/**
 * Created by farhan on 04/12/17.
 */

public class LoggingFacade {

    /*facade class to decouple logging and crashytics*/

    private static final String GIT_SHA = "git_sha";
    private static final String BUILD_TIME = "build_time";


    static void enableLogging(){
        if (BuildConfig.ENABLE_CRASHLYTICS) {
//            Fabric.with(DearOApplication.sContext, new Crashlytics());
//            Crashlytics.setString(GIT_SHA,BuildConfig.GIT_SHA);
//            Crashlytics.setString(BUILD_TIME, BuildConfig.BUILD_TIME);
        }
    }

    public static void log(String key, String value) {
        if (BuildConfig.ENABLE_CRASHLYTICS) {
//            Crashlytics.setString(key, value);
        }
    }

    public static void log(String key, boolean value) {
        if (BuildConfig.ENABLE_CRASHLYTICS) {
//            Crashlytics.setBool(key, value);
        }
    }

    public static void log(Throwable e) {
        if (BuildConfig.ENABLE_CRASHLYTICS) {
//            Crashlytics.logException(e);
        }
    }


}
