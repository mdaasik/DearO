package com.carworkz.dearo.splashscreen

import com.carworkz.dearo.MainActivity
import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

/**
 * Created by Farhan on 26/10/17.
 */
@ActivityScoped
@Subcomponent(modules = [(ConfigPresenterModule::class)])
interface ConfigComponent {
    fun inject(activity: SplashScreenActivity)
    fun inject(activity: MainActivity)
}