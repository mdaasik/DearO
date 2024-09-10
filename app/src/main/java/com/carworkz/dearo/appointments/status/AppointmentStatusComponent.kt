package com.carworkz.dearo.appointments.status

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(AppointmentStatusPresenterModule::class)])
interface AppointmentStatusComponent {
    fun inject(appointmentStatusChangeActivity: AppointmentStatusChangeActivity)
}