package com.carworkz.dearo.appointments.appointmentdetails

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class AppointmentCardDetailsPresenterModule(val view: AppointmentCardDetailsContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): AppointmentCardDetailsContract.View {
        return view
    }
}
