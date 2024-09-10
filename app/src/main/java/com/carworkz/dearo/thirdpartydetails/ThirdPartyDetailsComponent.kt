package com.carworkz.dearo.thirdpartydetails

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [ThirdPartyDetailsPresenterModule::class])
interface ThirdPartyDetailsComponent {

    fun inject(activity: ThirdPartyDetailsActivity)
}