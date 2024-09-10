package com.carworkz.dearo.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.carworkz.dearo.R;
import com.carworkz.dearo.base.BaseActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Farhan on 8/8/17.
 */

public class PermissionUtil {

    public static String[] STORAGE_USAGE_PERMISSIONS;
    private static String[] CAMERA_USAGE_PERMISSIONS;
    private static String[] SMS_PERMISSIONS;
    private static final int PERM_REQUEST_CODE = 1;
    private static Map<String[], Integer> combinedExpMap;
    private static final String SPACE = " ";


    final static class DialogListener implements DialogInterface.OnDismissListener, DialogInterface.OnClickListener {
        final BaseActivity activity;
        final ActivityCompat.OnRequestPermissionsResultCallback callback;
        final String[] permToRequestArray;
        DialogListener(BaseActivity baseActivity, String[] strArr, ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback) {
            this.activity = baseActivity;
            this.permToRequestArray = strArr;
            this.callback = onRequestPermissionsResultCallback;
        }
        @Override
        public void onDismiss(DialogInterface dialog) {
            PermissionUtil.requestPermissions(activity, this.permToRequestArray, this.callback);
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    static {
        CAMERA_USAGE_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
        SMS_PERMISSIONS = new String[]{"android.permission.RECEIVE_SMS", "android.permission.READ_SMS"};
        Map hashMap = new HashMap();
        combinedExpMap = hashMap;
        hashMap.put(CAMERA_USAGE_PERMISSIONS, R.string.permission_camera_and_storage_permission_combined_explanation);
        STORAGE_USAGE_PERMISSIONS = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};

    }
    public static String[] getCameraUsagePermissionGroup() {
        return CAMERA_USAGE_PERMISSIONS;
    }

    public static String[] getSmsPermissionGroup() {
        return SMS_PERMISSIONS;
    }

    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == 0;
    }
    public static boolean checkPermissions(Context context, String[] permissionsArray) {
        int length = permissionsArray.length;
        int i = 0;
        while (i < length) {
            boolean checkPermission = checkPermission(context, permissionsArray[i]);
            if (!checkPermission) {
                return false;
            }
            i++;
        }
        return true;
    }

    public static void requestPermissions(BaseActivity activity, String[] permArr, int[] permExpStrResourceArr,
                                          ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback) {
        if (permExpStrResourceArr == null || permArr.length == permExpStrResourceArr.length) {
            StringBuilder sbPermMsg = null;
            Resources resources = activity.getResources();
            List listOfDeniedPerms = new LinkedList();
            int[] permGrantResults = new int[permArr.length];
            for (int i = 0; i < permArr.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permArr[i]) == 0) {
                    permGrantResults[i] = 0;
                } else {
                    boolean shouldShowRequestPermissionRationale;
                    listOfDeniedPerms.add(permArr[i]);
                    try {
                        shouldShowRequestPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permArr[i]);
                    } catch (Throwable e) {
                        shouldShowRequestPermissionRationale = false;
                    }
                    if (shouldShowRequestPermissionRationale && permExpStrResourceArr != null) {
                        if (sbPermMsg == null) {
                            sbPermMsg = new StringBuilder();
                            sbPermMsg.append(resources.getString(R.string.permission_explanation_header));
                        }
                        sbPermMsg.append(new StringBuilder(SPACE).append(resources.getString(permExpStrResourceArr[i])).toString());
                    }
                }
            }
            if (listOfDeniedPerms.size() == 0) {
                onRequestPermissionsResultCallback.onRequestPermissionsResult(PERM_REQUEST_CODE, permArr, permGrantResults);
                return;
            }
            String[] arrayOfDeniedPerms = new String[listOfDeniedPerms.size()];
            listOfDeniedPerms.toArray(arrayOfDeniedPerms);
            if (sbPermMsg != null) {
                String sbPermMsgFinal = sbPermMsg.toString();
                if (listOfDeniedPerms.size() == permArr.length && combinedExpMap.containsKey(permArr)) {
                    StringBuilder sbPermMsgCombined = new StringBuilder();
                    sbPermMsgCombined.append(resources.getString(R.string.permission_explanation_header));
                    sbPermMsgCombined.append(SPACE);
                    sbPermMsgCombined.append(resources.getString(combinedExpMap.get(permArr)));
                    sbPermMsgFinal = sbPermMsgCombined.toString();
                }
                showPermissionExplanationDialog(activity, sbPermMsgFinal, arrayOfDeniedPerms, onRequestPermissionsResultCallback);
                return;
            }
            requestPermissions(activity, arrayOfDeniedPerms, onRequestPermissionsResultCallback);
            return;
        }
        throw new IllegalArgumentException("permissions and explanation string ids should have the same length");
    }

    public static void requestPermissions(BaseActivity baseActivity, String permission, int permExpStrResource, ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback) {
        String[] permArr = new String[1];
        permArr[0] = permission;
        int[] permExpStrResourceArr = new int[1];
        permExpStrResourceArr[0] = permExpStrResource;
        requestPermissions(baseActivity, permArr, permExpStrResourceArr, onRequestPermissionsResultCallback);
    }

    public static void requestPermissions(BaseActivity baseActivity, String[] permArr,
                                           ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback) {
        baseActivity.setPermissionsResultCallback(onRequestPermissionsResultCallback);
        ActivityCompat.requestPermissions(baseActivity, permArr, PERM_REQUEST_CODE);
//        savePermissionRequested(Arrays.asList(strArr));
    }
    //    private static void savePermissionRequested(Collection<String> collection) {
//        PreferencesManager persisted = Preferences.persisted();
//        Set stringSet = persisted.getStringSet(Constants.PREF_APP_PERMISSION_REQUESTS, new HashSet());
//        stringSet.addAll(collection);
//        persisted.set(Constants.PREF_APP_PERMISSION_REQUESTS, stringSet);
//    }
//
//    public static boolean isPermissionDeniedPermanently(Activity activity, String str) {
//        if (checkPermission(activity, str) || ActivityCompat.shouldShowRequestPermissionRationale(activity, str)) {
//            return false;
//        }
//        return Preferences.persisted().getStringSet(Constants.PREF_APP_PERMISSION_REQUESTS, Collections.EMPTY_SET).contains(str);
//    }
    private static void showPermissionExplanationDialog(BaseActivity activity, String permissionExpMsg, String[] permissionsArray, ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback) {
        DialogListener listener = new DialogListener(activity, permissionsArray, onRequestPermissionsResultCallback);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(permissionExpMsg);
        builder.setPositiveButton("Next", listener);
        builder.setOnDismissListener(listener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
