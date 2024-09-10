package com.carworkz.dearo.login.addmobilenumber;

import com.carworkz.dearo.injection.ActivityScoped;

import dagger.Subcomponent;

/**
 * Created by Farhan on 31/7/17.
 */

@ActivityScoped
@Subcomponent(modules = LoginPresenterModule.class)
public interface LoginComponent {

    void inject(LoginActivity loginActivity);

    // void inject(MainActivity mainActivity);

    //LoginPresenter LOGIN_PRESENTER();
}
