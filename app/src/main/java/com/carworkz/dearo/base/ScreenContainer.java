package com.carworkz.dearo.base;

//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
/**
 * Created by Farhan on 8/8/17.
 */

public interface ScreenContainer {

    ViewGroup bind(AppCompatActivity activity);

    Toolbar getToolbar();

}
