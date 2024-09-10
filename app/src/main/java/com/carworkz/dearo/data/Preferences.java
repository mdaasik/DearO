package com.carworkz.dearo.data;

import com.carworkz.dearo.DearOApplication;
import com.carworkz.dearo.SharedPreferenceManager;

/**
 * Created by Farhan on 21/8/17.
 */

public class Preferences {

    public static final String USER = "user";
    public static final String APP = "app";


    private static SharedPreferenceManager sUserInstance;
    private static SharedPreferenceManager sAppInstance;


    public static SharedPreferenceManager user() {
        if (sUserInstance == null) {
            sUserInstance = new SharedPreferenceManager(DearOApplication.sContext, USER);
        }
        return sUserInstance;
    }

    public static SharedPreferenceManager app() {
        if (sAppInstance == null) {
            sAppInstance = new SharedPreferenceManager(DearOApplication.sContext, APP);
        }
        return sAppInstance;
    }




}
