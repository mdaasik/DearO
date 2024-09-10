package com.carworkz.dearo.addjobcard.completejobcard

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(CompleteJobCardPresenterModule::class)])
interface CompleteJobCardComponent
{
    fun inject(activity: CompleteJobCardActivity)
}