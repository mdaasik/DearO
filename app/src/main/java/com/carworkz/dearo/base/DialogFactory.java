package com.carworkz.dearo.base;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Space;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import com.carworkz.dearo.R;

/**
 * Created by Farhan on 9/11/17.
 */

public final class DialogFactory {

    @SuppressWarnings("WeakerAccess")
    public static AlertDialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }


    public static AlertDialog createSimpleOkErrorDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, onClickListener);
        return alertDialog.create();
    }

    public static AlertDialog createSimpleOkErrorDialog(Context context,
                                                        @StringRes int titleResource,
                                                        @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    @SuppressWarnings("WeakerAccess")
    public static AlertDialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog.create();

        }


    public static AlertDialog createGenericErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    @SuppressWarnings("WeakerAccess")
    public static AlertDialog createGenericErrorDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, onClickListener);
        return alertDialog.create();
    }


    @SuppressWarnings("WeakerAccess")
    public static AlertDialog createGenericErrorDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, onClickListener);
        return alertDialog.create();
    }

    public static AlertDialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    public static AlertDialog notifyAlertDialog(final Context context, String title, String message, final Boolean showNotify, Boolean defaultChecked, Boolean showVerify, final NotifyButtonListener listner) {
        View checkBoxView = View.inflate(context, R.layout.notify_check, null);
        final CheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
        final AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        alert.setMessage(message);
        if (showNotify) {
            checkBox.setChecked(defaultChecked);
            alert.setView(checkBoxView);
        } else {
            checkBox.setVisibility(View.GONE);
        }
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (showNotify) {
                    listner.positiveButton(checkBox.isChecked());
                } else {
                    listner.positiveButton(false);
                }
            }
        });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        if (showVerify) {
            alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Verify", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listner.neutralButton();
                }
            });
        }
        return alert;
    }

    public interface NotifyButtonListener {

        void positiveButton(Boolean notify);

        void neutralButton();
    }

}
