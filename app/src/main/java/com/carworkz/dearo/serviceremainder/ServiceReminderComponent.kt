package com.carworkz.dearo.serviceremainder

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(ServiceReminderPresenterModule::class)])
interface ServiceReminderComponent {
    fun inject(serviceReminderActivity: NewServiceReminderActivity)
}