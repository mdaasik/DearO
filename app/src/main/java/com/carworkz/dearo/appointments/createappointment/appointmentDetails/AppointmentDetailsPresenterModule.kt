package com.carworkz.dearo.appointments.createappointment.appointmentDetails

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Module
import dagger.Provides

/**
 * Created by kush on 24/11/17.
 */
@Module
class AppointmentDetailsPresenterModule(val view: AppointmentDetailsContract.View) {
    @Provides
    @FragmentScoped
    fun providesView(): AppointmentDetailsContract.View = view
}