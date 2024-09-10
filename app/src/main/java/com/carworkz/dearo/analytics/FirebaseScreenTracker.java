package com.carworkz.dearo.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

class FirebaseScreenTracker implements ScreenTracker {

    private final FirebaseAnalytics firebaseAnalytics;

    @Inject
    FirebaseScreenTracker(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void sendScreenEvent(Activity activity, String screenName, String className) {
//        firebaseAnalytics.setCurrentScreen(activity, screenName, null);
        Bundle params = new Bundle();
        params.putString("Screen_Name", screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW,params);
    }
}
