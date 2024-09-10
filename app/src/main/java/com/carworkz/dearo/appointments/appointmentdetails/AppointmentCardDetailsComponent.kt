package com.carworkz.dearo.appointments.appointmentdetails

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(AppointmentCardDetailsPresenterModule::class)])
interface AppointmentCardDetailsComponent {

    fun inject(appointmentCardDetailsActivity: AppointmentCardDetailsActivity)
}