package com.carworkz.dearo.appointments.reschedule

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class ReschedulePresenterModule(val view: RescheduleContract.View) {
    @Provides
    @ActivityScoped
    fun providesView(): RescheduleContract.View = view
}