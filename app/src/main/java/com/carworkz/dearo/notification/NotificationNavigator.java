package com.carworkz.dearo.notification;


import android.content.Context;
import android.os.Bundle;

import com.carworkz.dearo.JsonStringToMapAdapter;
import com.carworkz.dearo.domain.entities.Notification;
import com.carworkz.dearo.notification.commands.NotificationCommand;
import com.carworkz.dearo.notification.commands.TestCommand;
import com.carworkz.dearo.utils.Utility;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import timber.log.Timber;

public class NotificationNavigator {

    public static final String ARG_TASK = "task";
    private static final String TASK_NOTIFICATION = "notification_task";
    private static final String TASK_TEST = "test_task";
    private static final Map<String, FcmCommand> MESSAGE_RECEIVERS;

    static {
        Map<String, FcmCommand> receivers = new HashMap<>();
        receivers.put(TASK_TEST, new TestCommand());
        receivers.put(TASK_NOTIFICATION, new NotificationCommand());
        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }

    private Context context;

    @Inject
    public NotificationNavigator(Context context) {
        this.context = context;
    }

    public void navigate(Bundle notificationData) {
        Timber.d("Inside navigator " + notificationData);

//        if (notificationData.getString(ARG_TASK) == null) {
//            return;
//        }

        Map<String, Object> objectMap = Utility.convertBundleToMap(notificationData);

        Moshi moshi = new Moshi.Builder()
                .add(new JsonStringToMapAdapter())
                .add(new KotlinJsonAdapterFactory())
                .build();


        ParameterizedType types = Types.newParameterizedType(Map.class, String.class, Object.class);
        String json = moshi.adapter(types).toJson(objectMap);
        JsonAdapter<Notification> jsonAdapter = moshi.adapter(Notification.class);
        try {
            Notification notification = jsonAdapter.fromJson(json);
            String command = Objects.requireNonNull(notification).getTask();
            FcmCommand fcmCommand = MESSAGE_RECEIVERS.get(command);
            if (fcmCommand == null) {
                Timber.d("Unknown command recieved " + command);
            } else {
                fcmCommand.executeCommand(context, command, notification);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
