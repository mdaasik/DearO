package com.carworkz.dearo.amc.amcsolddetails

import com.carworkz.dearo.amc.amcdetails.AmcDetailsActivity
import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [AmcDetailsPresenterModule::class])
interface AmcDetailsComponent {
    fun inject(view: AmcSoldDetailsActivity)
    fun inject(view: AmcDetailsActivity)
}