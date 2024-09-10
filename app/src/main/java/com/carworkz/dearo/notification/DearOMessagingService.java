package com.carworkz.dearo.notification;

import android.os.Bundle;

import com.carworkz.dearo.DearOApplication;
import com.carworkz.dearo.cronjobs.JobHelper;
import com.carworkz.dearo.data.local.SharedPrefHelper;
import com.carworkz.dearo.utils.Utility;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import timber.log.Timber;


public class DearOMessagingService extends FirebaseMessagingService {

    NotificationNavigator notificationNavigator;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Timber.d("did i get the msg + " + remoteMessage.getNotification());
        Timber.d("is navigator null " + notificationNavigator);
        notificationNavigator = ((DearOApplication) getApplicationContext())
                .getApplicationComponent()
                .getNavigator();

        Objects.requireNonNull(notificationNavigator, "navigator is null").navigate(getNotificationBundle(remoteMessage));
    }


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Timber.d("refreshed token: " + token);
        if (!SharedPrefHelper.getUserAccessToken().isEmpty()) {
            SharedPrefHelper.setFcmToken(token);
            JobHelper.scheduleJob(getApplicationContext(), JobHelper.JOB_DEVICE_REGISTRAR);
        }
    }

    private Bundle getNotificationBundle(RemoteMessage remoteMessage) {
        Bundle bundle = Utility.convertMapToBundle(remoteMessage.getData());
        if (remoteMessage.getNotification() != null) {
            bundle.putString("title", remoteMessage.getNotification().getTitle());
            bundle.putString("message", remoteMessage.getNotification().getBody());
        }
        return bundle;
    }
}
