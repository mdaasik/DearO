package com.carworkz.dearo.cronjobs.userconfig

import dagger.Module
import dagger.Provides
import javax.inject.Inject

/**
 * Created by Farhan on 18/10/17.
 */
@Module
class UserConfigPresenterModule @Inject constructor(val view: UserConfigContract.View) {

    @Provides
    fun providesView(): UserConfigContract.View {
        return view
    }
}