package com.carworkz.dearo.partfinder

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(PartFinderPresenterModule::class)])
interface PartFinderComponent {

    fun inject(activity: PartFinderActivity)
}