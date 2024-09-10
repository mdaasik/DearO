package com.carworkz.dearo.appointments.createappointment.appointmentDetails

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

@FragmentScoped
@Subcomponent(modules = [(AppointmentDetailsPresenterModule::class)])
interface AppointmentDetailsComponent {
    fun inject(appointmentVehicleDetailsFragment: AppointmentVehicleDetailsFragment)
}