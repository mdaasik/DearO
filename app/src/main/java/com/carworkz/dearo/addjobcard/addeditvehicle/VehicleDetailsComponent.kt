package com.carworkz.dearo.addjobcard.addeditvehicle

import com.carworkz.dearo.injection.ActivityScoped

import dagger.Subcomponent

/**
 * Created by kush on 18/8/17.
 */
@ActivityScoped
@Subcomponent(modules = [VehicleDetailsPresenterModule::class])
interface VehicleDetailsComponent {
    fun inject(vehicleDetailsActivity: VehicleDetailsActivity)
}
