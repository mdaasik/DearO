package com.carworkz.dearo.notification.commands;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.carworkz.dearo.BuildConfig;
import com.carworkz.dearo.MainActivity;
import com.carworkz.dearo.R;
import com.carworkz.dearo.domain.entities.Notification;
import com.carworkz.dearo.mycustomers.mycustomerlisting.MyCustomersActivity;
import com.carworkz.dearo.notification.FcmCommand;
import java.util.Map;
import java.util.Objects;
import timber.log.Timber;
import static com.carworkz.dearo.analytics.ScreenTracker.SCREEN_MY_CUSTOMERS;
import static com.carworkz.dearo.extensions.ExtensionsKt.toast;

public class NotificationCommand extends FcmCommand {


    @Override
    public void executeCommand(Context context, String type, Notification notification) {
        Timber.d("Received FCM message " + type);
        Timber.d("Parsing FCM notification command " + notification.toString());

        if (!BuildConfig.NotificationVersion.equals(notification.getVersion())) {
            Timber.d("Notification version are different. Ignoring..");
            if (BuildConfig.DEBUG) {
                toast(context, "Notification version are different. Ignoring..");
            }
        }

        switch (Objects.requireNonNull(notification.getScreen())) {
            case SCREEN_MY_CUSTOMERS: {
                Map<String, String> extras = notification.getOptions();
                if (extras != null) {
                    Timber.d("extras available, lets roll");
                    Intent intent = MyCustomersActivity.getIntent(context, extras.get(MyCustomersActivity.ARG_START_DATE), extras.get(MyCustomersActivity.ARG_END_DATE));

                    //title/message is null when notifcation is handled by system.
                    if (notification.getTitle() != null && notification.getMessage() != null) {

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.default_notification_channel_id))
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.ic_dearo_notification)
                                .setContentTitle(notification.getTitle())
                                .setContentText(notification.getMessage())
                                .setCategory(notification.getCategory())
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setContentIntent(PendingIntent.getActivity(context, 200, intent, PendingIntent.FLAG_CANCEL_CURRENT))
                                .setAutoCancel(true);

                        NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            String channelId = context.getString(R.string.default_notification_channel_id);
                            NotificationChannel channel = new NotificationChannel(channelId,
                                    context.getString(R.string.app_name),
                                    NotificationManager.IMPORTANCE_DEFAULT);
                            Objects.requireNonNull(notificationManager, "Wow Unlucky day, Notification manager is null! Go Home Boy").createNotificationChannel(channel);
                            builder.setChannelId(channelId);
                        }

                        Objects.requireNonNull(notificationManager, "Wow Unlucky day, Notification manager is null! Go Home Boy").notify(notification.getSignature()
                                , builder.build());
                    } else {
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
                break;
            }
            default: {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
                break;
            }
        }
        Timber.d("Parsing successful " + notification.toString());
    }
}
