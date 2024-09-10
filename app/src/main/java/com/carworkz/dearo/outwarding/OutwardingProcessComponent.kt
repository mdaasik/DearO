package com.carworkz.dearo.outwarding

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(OutwardingProcessPresenterModule::class)])
interface OutwardingProcessComponent {
    fun inject(activity: OutwardingProcessActivity)
}