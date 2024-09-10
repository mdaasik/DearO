package com.carworkz.dearo.appointments.status

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class AppointmentStatusPresenterModule(val view: AppointmentStatusContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): AppointmentStatusContract.View {
        return view
    }
}
