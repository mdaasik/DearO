package com.carworkz.dearo.notification;

import android.content.Context;

import com.carworkz.dearo.domain.entities.Notification;


/**
 * Represents the client response when an FCM ping is received. Each type of FCM ping should have
 * an FcmCommand implementation associated with it.
 */
public abstract class FcmCommand {

    /**
     * Defines behavior when FCM is received.
     */
    public abstract void executeCommand(Context context, String type, Notification extraData);
}
