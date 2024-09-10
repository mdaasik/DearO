package com.carworkz.dearo.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.carworkz.dearo.R;
import com.carworkz.dearo.base.BaseActivity;
import com.carworkz.dearo.databinding.InsuranceCompanyDetailsBinding;
import com.carworkz.dearo.databinding.InsuranceDetailsBinding;
import com.carworkz.dearo.databinding.LayoutAmcDetailsBinding;
import com.carworkz.dearo.databinding.LayoutFirDetailsBinding;
import com.carworkz.dearo.databinding.LayoutSurveyDetailBinding;
import com.carworkz.dearo.notification.NotificationNavigator;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import timber.log.Timber;

public class Utility {

    public static final String TIMEZONE_UTC = "UTC";
    public static final String TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_2 = "MMM d, hh:mm a";
    public static final String DATE_FORMAT_3 = "EEE, MMM d, yyyy";
    public static final String DATE_FORMAT_4 = "dd MMM yyyy";
    public static final String DATE_FORMAT_5 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_6 = "MMM d, yyyy";
    public static final String DATE_FORMAT_7 = "dd-MM-yyyy";
    public static final String DATE_FORMAT_8 = "d MMM, hh:mm a";
    public static final String DATE_FORMAT_9 = "EEE,MMM d,''yy"; //Wed, Jul 4, '01
    public static final String DATE_FORMAT_10 = "MMM d, hh:mm a"; //Jan 21, 3:04 PM


    private static final String TAG = "Utility";
    private static final Pattern VALID_PLAIN_MOBILE_NUMBER_REGEX =
            Pattern.compile("^[6789]\\d{9}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_PHONE_NUMBER_REGEX =
            Pattern.compile("^(\\+\\d{1,3}[- ]?)?\\d{10}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_PHONE_NUMBER_REGEX1 =
            Pattern.compile("^((\\+?\\d[- ]?)|(\\+\\d{1,3}[- ]?))?\\d{10}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_REGISTRATION_NUMBER =
           // Pattern.compile("^[A-Z]{2}[0-9]{1,2}[0-9A-Z]{1,3}[0-9]{1,4}$", Pattern.CASE_INSENSITIVE);
            Pattern.compile("^[0-9a-z](\\/?[0-9a-z])*\\/?$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_DECIMAL_PRICE = Pattern.compile("^\\d+(\\.\\d{1,4})?$");
    private static final Pattern VALID_GST_NUMBER =
            Pattern.compile("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][A-Z1-9]Z[0-9A-Z]");
    @Inject
    NotificationNavigator notificationNavigator;

    public static boolean isMobileNumberValid(String mobileNo) {
//        if (mobileNo.length() != 10) {
//            return false;
//        } else {
        if (!isEmpty(mobileNo)) {
            mobileNo = mobileNo.replaceAll(" ", "");
            Timber.d("checking for mobile no " + mobileNo);
            Matcher matcher = VALID_PHONE_NUMBER_REGEX1.matcher(mobileNo);
            return matcher.matches();
        }
        return false;
//        }
    }

    public static boolean isMobileNumber(String mobileNo) {
        if (!isEmpty(mobileNo)) {
            mobileNo = mobileNo.replaceAll(" ", "");
            Timber.d("checking for mobile no " + mobileNo);
            Matcher matcher = VALID_PLAIN_MOBILE_NUMBER_REGEX.matcher(mobileNo);
            return matcher.matches();
        }
        return false;
    }

    public static String getServerAcceptableContactNumber(String mobileNumber) {
        if (mobileNumber != null && isMobileNumberValid(mobileNumber)) {
            mobileNumber = mobileNumber.replaceAll(" ", "");
            return mobileNumber.substring(mobileNumber.length() - 10);
        } else {
            throw new IllegalArgumentException("Mobile number is not valid");
        }
    }

    public static String getServerAcceptableRegistrationNumber(String regNumber) {
        if (regNumber != null && isRegistrationNumberValid(regNumber)) {
            return regNumber.replaceAll(" ", "").replaceAll("-", "");
        } else {
            throw new IllegalArgumentException("Reg number is not valid");
        }
    }

    public static boolean isRegistrationNumberValid(String number) {
        number = number.replaceAll(" ", "").replaceAll("-", "");
        Matcher matcher = VALID_REGISTRATION_NUMBER.matcher(number);
        return matcher.matches();
    }

    public static boolean isEmailValid(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }

    public static boolean isEmpty(CharSequence sequence){
        return sequence == null || sequence.length() == 0;
    }

    public static boolean isPinCodeValid(@Nullable String code) {
        if(code == null)
            return false;

        return code.length() == 6;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context == null)
            return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null || activity.isFinishing())
            return;

        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        View currentView = activity.getCurrentFocus();

        if (inputMethodManager != null && currentView != null && !activity.isFinishing() && currentView.getWindowToken() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    currentView.getWindowToken(), 0);
        }
    }

    public static void disableStartKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public static String formatDate(String dateFormat, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        return sdf.format(calendar.getTime());
    }

    public static String formatDate(String date, String inputType, String outputType, String sourceTimezone) {
        if (date == null)
            return "";
        Date formatedDate = null;
        SimpleDateFormat sdfPreFormatted = new SimpleDateFormat(inputType, Locale.ENGLISH);
        sdfPreFormatted.setTimeZone(TimeZone.getTimeZone(sourceTimezone));
        try {
            formatedDate = sdfPreFormatted.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(outputType, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        String s;
        s = sdf.format(formatedDate);
        Timber.d(s + TimeZone.getDefault().getDisplayName());
        return s;
    }

    public static String formatTime(int hour, int minute) {
        String format, hourString = hour + "", minuteString = minute + "";
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        if (hour < 10) {
            hourString = "0" + hour;
        }

        if (minute < 10) {
            minuteString = "0" + minute;
        }
        return hourString + ":" + minuteString + " " + format;
    }

    public static String formatToServerTime(@NonNull Date date, @NonNull String outputType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(outputType, Locale.ENGLISH);
        return dateFormat.format(date);
    }

    public static String dateToCalender(Date date, String inputType) {
        SimpleDateFormat sdf = new SimpleDateFormat(inputType, Locale.ENGLISH);
        return sdf.format(date);
    }

    public static Calendar dateToCalender(@NotNull String date, String inputType) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(inputType, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
        try {
            cal.setTime(sdf.parse(date));
            Timber.d("Date: " + dateToCalender(cal.getTime(), TIMESTAMP));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    public static Date getDate(String dateString) throws NullPointerException {
        if (dateString == null) {
            throw new NullPointerException("Date cannot be Null");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void checkNotNull(Object o, String exceptionMessage) {
        if (o == null) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    public static void expandOrCollapseView(final View v, @Nullable final AnimationCallback callback) {
        if (v.getVisibility() == View.GONE) {
            v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final int targetHeight = v.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.getLayoutParams().height = 1;
            v.setVisibility(View.VISIBLE);
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            ? ViewGroup.LayoutParams.WRAP_CONTENT
                            : (int) (targetHeight * interpolatedTime);
                    v.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // 1dp/ms
            a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    assert callback != null;
                    callback.onAnimationEnd(AnimationCallback.Toggle.UP);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            v.startAnimation(a);
        } else {
            final int initialHeight = v.getMeasuredHeight();

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        v.setVisibility(View.GONE);
                    } else {
                        v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // 1dp/ms
            a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    assert callback != null;
                    callback.onAnimationEnd(AnimationCallback.Toggle.DOWN);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            v.startAnimation(a);
        }
    }

    public static String timeRemaining(String dateString) {

        if (dateString == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Calendar now = Calendar.getInstance(); //get current time
        long millisLeft = cal.getTimeInMillis() - now.getTimeInMillis();

        long hoursDiff = millisLeft / (60 * 60 * 1000);
        long minDiff = (millisLeft % (60 * 60 * 1000)) / (60 * 1000);

        //System.out.println("Days left : " + dayDiff);
//        System.out.println("Hours left : " + hoursDiff);
//        System.out.println("Minutes left : " + minDiff);

        String timeRemaining = "";
        if (Math.abs(hoursDiff) > 0) {
            timeRemaining += Math.abs(hoursDiff);
        } else {
            timeRemaining += "0";
        }
        if (Math.abs(minDiff) > 0) {
            String minutes = String.format(Locale.ENGLISH, "%02d", Math.abs(minDiff));
            timeRemaining += ":" + minutes + " hrs";
        } else {
            timeRemaining += ":00 hrs";
        }

        if (cal.equals(now)) {
            timeRemaining = "Now";
        } else if (cal.before(now)) {
            timeRemaining += " ago";
        } else {
            timeRemaining += " remaining";
        }

        return timeRemaining;


    }

    public static String newTimeRemaining(String dateString) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("TIMEZONE_UTC"));
        Calendar cal = Calendar.getInstance();

        cal.setTime(sdf.parse(dateString));


        Calendar now = Calendar.getInstance(); //get current time
        long millisLeft = cal.getTimeInMillis() - now.getTimeInMillis();

        long hoursDiff = millisLeft / (60 * 60 * 1000);
        long minDiff = (millisLeft % (60 * 60 * 1000)) / (60 * 1000);

        //System.out.println("Days left : " + dayDiff);
        System.out.println("Hours left : " + hoursDiff);
        System.out.println("Minutes left : " + minDiff);

        String timeRemaining = "";
        if (Math.abs(hoursDiff) > 0) {
            timeRemaining += Math.abs(hoursDiff);
        } else {
            timeRemaining += "0";
        }
        if (Math.abs(minDiff) > 0) {
            String minutes = String.format(Locale.ENGLISH, "%02d", Math.abs(minDiff));
            timeRemaining += ":" + minutes + " hrs";
        } else {
            timeRemaining += ":00 hrs";
        }

        if (cal.equals(now)) {
            timeRemaining = "Now";
        } else if (cal.before(now)) {
            timeRemaining += " ago";
        } else {
            timeRemaining += " remaining";
        }

        return timeRemaining;


    }

    public static void makeCall(final Context context, final String mobileNo) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            Toast.makeText(context, "Telephone services not available", Toast.LENGTH_SHORT).show();
            return;
        }
        if (PermissionUtil.checkPermission(context, Manifest.permission.CALL_PHONE)) {
            try{
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                context.startActivity(callIntent.setData(Uri.parse("tel:" + mobileNo)));
            }catch (ActivityNotFoundException e){
                Timber.d("ActivityNotFoundException "+e);
                Toast.makeText(context, "No Application found to call", Toast.LENGTH_SHORT).show();
            }
        } else {
            PermissionUtil.requestPermissions((BaseActivity) context, Manifest.permission.CALL_PHONE, R.string.permission_call_explanation, new ActivityCompat.OnRequestPermissionsResultCallback() {
                @Override
                public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        try{
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            context.startActivity(callIntent.setData(Uri.parse("tel:" + mobileNo)));
                        }catch (ActivityNotFoundException e){
                            Timber.d("ActivityNotFoundException "+e);
                            Toast.makeText(context, "No Application found to call",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    public static int dpToPx(Context context, int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }

    @SuppressWarnings("WeakerAccess")
    public static float spToPx(Context context, float sp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics()));
    }

    public static float convertDpToSp(int dp, Context context) {
        return dpToPx(context, dp) / spToPx(context, dp);
    }

    public static Double round(@NonNull Double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public static Double round(@NonNull Double value) {
        if (value < 0.0) return 0.0;
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(value));
    }

    public static <T> int findIndexFromList(T searchObj, List<T> searchList) {
        if (searchObj == null)
            return -1;

        return searchList.indexOf(searchObj);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getListFromJsonArray(JSONArray jsonArray) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add((T) jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;

    }

    public static String getMimeType(File file) {
        Uri selectedUri = Uri.fromFile(file);
        String fileExtension
                = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
    }

    public static String convertToCurrency(Double amount) {
        if (amount == null)
            return "";
        amount = round(amount, 1);
        return "\u20B9 " + amount;
//        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
//        return formatter.format(amount);
    }

    public static Boolean isValidDecimal(String price) {
        Matcher matcher = VALID_DECIMAL_PRICE.matcher(price);
        return matcher.matches();
    }

    public static Boolean isValidGst(String gst) {
        Matcher matcher = VALID_GST_NUMBER.matcher(gst);
        return matcher.matches();
    }

    public static Double amountToPercentage(double amount, double total) {
        return (amount / total) * 100;
    }

    public static Double percentageToAmount(double percentage, double total) {
        return (percentage / 100) * total;
    }

    public @Nullable
    static Drawable getTinted(Context context, @DrawableRes int res, @ColorInt int color) {
        // need to mutate otherwise all references to this drawable will be tinted
        Drawable drawable = ContextCompat.getDrawable(context, res).mutate();
        return tint(drawable, ColorStateList.valueOf(color));
    }

    static public <K, V> List<K> getKeys(Map<K, V> map) {
        return new ArrayList<>(map.keySet());
    }

    public static Drawable tint(Drawable input, ColorStateList tint) {
        if (input == null) {
            return null;
        }
        Drawable wrappedDrawable = DrawableCompat.wrap(input);
        DrawableCompat.setTintList(wrappedDrawable, tint);
        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.MULTIPLY);
        return wrappedDrawable;
    }

    public static void isJobSchedulerRunning(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        for (JobInfo jobInfo : Objects.requireNonNull(scheduler, "Scheduler is null").getAllPendingJobs()) {
            switch (jobInfo.getId()) {
                case 1: {
                    Timber.d("ForceUpdateService running");
                }

                case 2: {
                    Timber.d("UserConfigService running");

                }

                case 3: {
                    Timber.d("ImageUploadJob running");
                }
            }

        }
    }

    public static boolean isJobRunning(Context context, int jobId) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        for (JobInfo jobInfo : Objects.requireNonNull(scheduler, "Scheduler is null").getAllPendingJobs()) {
            if (jobInfo.getId() == jobId) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, Object> convertBundleToMap(Bundle resource) {
        Map<String, Object> map = new HashMap<>();
        for (String key : resource.keySet()) {
            map.put(key, resource.get(key));
        }
        Timber.d("converted map " + map);
        return map;
    }

    public static Bundle convertMapToBundle(Map<String, String> map) {
        Timber.d("converting map to bundle");
        Bundle bundle = new Bundle();
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = Objects.requireNonNull(activityManager).getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static void setVisibility(boolean visibility, View view) {
        if (view == null)
            return;

        if (visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public static void sendWhatsApp(Context context, String contact, String message) {
        PackageManager packageManager = context.getPackageManager();
        Timber.d("sending whatsapp to" + contact + " message " + message);
        Intent i = new Intent(Intent.ACTION_VIEW);

        contact = "+91" + contact;

        try {
            String url = "https://api.whatsapp.com/send?phone=" + contact + "&text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i);
            }else{
                Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.dialog_error_title, Toast.LENGTH_SHORT).show();
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalizeFirstChar(model);
        } else {
            return capitalizeFirstChar(manufacturer) + " " + model;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static String capitalizeFirstChar(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String capitalizeFirstCharOfWords(String s){
        String[] splitString = s.trim().split(" ");
        StringBuilder stringBuilder = new StringBuilder(splitString.length);
        for (String string : splitString) {
            stringBuilder.append(capitalizeFirstChar(string));
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static void expandOrCollapseView(@NotNull LayoutAmcDetailsBinding amcView, @NotNull AnimationCallback animationCallback) {

    }

    public static void expandOrCollapseView(@NotNull InsuranceDetailsBinding insuranceDetailView, @NotNull AnimationCallback animationCallback) {

    }

    public static void expandOrCollapseView(@NotNull InsuranceCompanyDetailsBinding insuranceCompanyDetailView, @NotNull AnimationCallback animationCallback) {

    }

    public static void expandOrCollapseView(@NotNull LayoutSurveyDetailBinding surveyorDetailView, @NotNull AnimationCallback animationCallback) {

    }

    public static void expandOrCollapseView(@NotNull LayoutFirDetailsBinding firDetailsView, @NotNull AnimationCallback animationCallback) {

    }
}
