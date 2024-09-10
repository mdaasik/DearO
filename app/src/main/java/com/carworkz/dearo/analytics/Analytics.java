package com.carworkz.dearo.analytics;

import android.content.Context;
import androidx.annotation.NonNull;

public class Analytics {

    private static volatile Analytics mAnalytics;


    private Analytics() {
        if (mAnalytics != null) {
            throw new RuntimeException("Use getInstance() to get singleton instance of the class");
        }
    }

    public static Analytics from() {
        return getInstance();
    }

    private static synchronized Analytics getInstance() {
        if (mAnalytics == null) {
            mAnalytics = new Analytics();
        }
        return mAnalytics;
    }

    public void trackScreen(@NonNull Context context, @NonNull String screenName) {

    }

}


