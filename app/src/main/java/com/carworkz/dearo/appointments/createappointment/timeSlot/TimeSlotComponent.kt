package com.carworkz.dearo.appointments.createappointment.timeSlot

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

/**
 * Created by kush on 8/12/17.
 */
@FragmentScoped
@Subcomponent(modules = [(TimeSlotPresenterModule::class)])
interface TimeSlotComponent {
    fun inject(timeSlotFragment: TimeSlotFragment)
}