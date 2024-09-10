package com.carworkz.dearo.cronjobs.forceupdatestatus

import dagger.Module
import dagger.Provides

/**
 * Created by Farhan on 8/11/17.
 */
@Module
class ForceUpdatePresenterModule(private val view: ForceUpdateContract.View) {

    @Provides
    fun providesView(): ForceUpdateContract.View {
        return view
    }
}