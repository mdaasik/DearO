package com.carworkz.dearo.addjobcard.createjobcard.customercarsearch

import com.carworkz.dearo.amc.AMCSearchActivity
import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

/**
 * Created by kush on 17/8/17.
 */
@ActivityScoped
@Subcomponent(modules = [(CustomerCarSearchPresenterModule::class)])
interface CustomerCarSearchComponent {
    fun inject(customerActivity: CustomerCarSearchActivity)
    fun inject(customerActivity: AMCSearchActivity)
}
