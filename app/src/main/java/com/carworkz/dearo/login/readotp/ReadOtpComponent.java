package com.carworkz.dearo.login.readotp;

import com.carworkz.dearo.injection.ActivityScoped;

import dagger.Subcomponent;

/**
 * Created by Farhan on 2/8/17.
 */
@ActivityScoped
@Subcomponent(modules = ReadOtpPresenterModule.class)
public interface ReadOtpComponent {

    void inject(ReadOtpActivity readOtpActivity);
}
