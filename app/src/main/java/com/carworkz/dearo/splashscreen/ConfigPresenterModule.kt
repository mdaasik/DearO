package com.carworkz.dearo.splashscreen

import dagger.Module
import dagger.Provides

/**
 * Created by Farhan on 26/10/17.
 */
@Module
class ConfigPresenterModule(val view: ConfigContract.View) {

    @Provides
    fun providesView(): ConfigContract.View {
        return view
    }
}