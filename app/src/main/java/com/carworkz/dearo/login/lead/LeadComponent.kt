package com.carworkz.dearo.login.lead

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(LeadPresenterModule::class)])
interface LeadComponent {
    fun inject(leadActivity: LeadActivity)
}