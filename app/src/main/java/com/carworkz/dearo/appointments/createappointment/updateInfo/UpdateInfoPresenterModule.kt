package com.carworkz.dearo.appointments.createappointment.updateInfo

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

/**
 * Created by Farhan on 8/1/18.
 */
@Module
class UpdateInfoPresenterModule(private val view: UpdateInfoContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): UpdateInfoContract.View {
        return view
    }
}