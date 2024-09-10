package com.carworkz.dearo.appointments.reschedule

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [ReschedulePresenterModule::class])
interface RescheduleComponent {
    fun inject(view: RescheduleActivity)
}