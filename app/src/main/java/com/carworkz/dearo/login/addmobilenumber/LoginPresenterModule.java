package com.carworkz.dearo.login.addmobilenumber;

import com.carworkz.dearo.injection.ActivityScoped;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Farhan on 31/7/17.
 */

@Module
public class LoginPresenterModule {

    private final LoginContract.View view;

    LoginPresenterModule(LoginContract.View view) {
        this.view = view;
    }


    @Provides
    @ActivityScoped
    public LoginContract.View providesView(){
        return view;
    }
}
