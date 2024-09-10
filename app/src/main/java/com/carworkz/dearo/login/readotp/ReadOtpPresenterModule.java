package com.carworkz.dearo.login.readotp;

import com.carworkz.dearo.injection.ActivityScoped;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Farhan on 2/8/17.
 */
@Module
public class ReadOtpPresenterModule {
    private final ReadOtpContract.View view;

    ReadOtpPresenterModule(ReadOtpContract.View view) {
        this.view = view;
    }


    @Provides
    @ActivityScoped
    public ReadOtpContract.View providesView(){
        return view;
    }
}
