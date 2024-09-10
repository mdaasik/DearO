package com.carworkz.dearo.vehiclequery

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(VehicleQueryPresenterModule::class)])
interface VehicleQueryComponent {
    fun inject(vehicleQueryActivity: VehicleQueryActivity)
}