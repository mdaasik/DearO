package com.carworkz.dearo.mrn

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [MrnPresenterModule::class])
interface MrnComponent {
    fun inject(activity: MrnActivity)
}