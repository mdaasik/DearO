package com.carworkz.dearo.amc

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(AMCPresenterModule::class)])
interface AmcComponent
{
    fun inject(activity: AmcListingActivity)
    fun inject(activity: AMCSuccessActivity)
}