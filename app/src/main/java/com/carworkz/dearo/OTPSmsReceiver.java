package com.carworkz.dearo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.content.LocalBroadcastManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * Created by Farhan on 10/02/2017.
 */

public class OTPSmsReceiver extends BroadcastReceiver {

    public static final String ARG_OTP_INTENT = "OTP_INTENT";
    public static final String ARG_OTP = "OTP";
    private SmsCallback callback;


    public OTPSmsReceiver(SmsCallback smsCallback) {
        this.callback = smsCallback;
    }


//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Bundle bundle = intent.getExtras();
//        if (bundle != null) {
//            final Object[] pdusObj = (Object[]) bundle.get("pdus");
//
//            assert pdusObj != null;
//            for (Object PdusObj : pdusObj) {
//
//                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) PdusObj);
//                String phoneNumber = currentMessage.getDisplayOriginatingAddress();
//
//                String message = currentMessage.getDisplayMessageBody();
//
//                message = message.substring(0, message.length());
//                Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; message: " + message);
//
//                getOTP(context, phoneNumber, message);
//
//            }
//        }
//    }



    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status status = extras.getParcelable(SmsRetriever.EXTRA_STATUS);
                switch (Objects.requireNonNull(status).getStatusCode()) {
                    case CommonStatusCodes.SUCCESS: {
                        String message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE);
                        Pattern pattern = Pattern.compile("\\d{6}");
                        Matcher matcher = pattern.matcher(Objects.requireNonNull(message));
                        Timber.d("onrecevie success otp" + message);
//                        Timber.d("onrecevie success otp" + Integer.parseInt(matcher.group(0)));
                        Timber.d("onrecevie success callback" + callback);

                        if (matcher.find() && callback != null) {
                            callback.onSmsReceived(Integer.parseInt(matcher.group(0)));
                        }
                        break;
                    }

                    case CommonStatusCodes.TIMEOUT: {
                        if (callback != null) {
                            callback.onTimeOut();
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }
    }

    public void setSmsCallback(SmsCallback smsCallback) {
        this.callback = smsCallback;
        Timber.d("setting call back" + callback);
    }

    private void getOTP(Context context, String phoneNumber, String message) {

        if (phoneNumber.contains("mDEARO")) {
            if (message.contains("OTP")) {
                int index = message.indexOf("OTP");
                String otp = message.substring(index + 4, index + 10);

                Intent otpIntent = new Intent(ARG_OTP_INTENT);
                otpIntent.putExtra(ARG_OTP, "" + otp);
                LocalBroadcastManager.getInstance(context).sendBroadcast(otpIntent);
            }
        }
    }


    public interface SmsCallback {
        void onSmsReceived(int otp);

        void onTimeOut();
    }
}
