package com.carworkz.dearo.addjobcard.addeditvehicle.addmissinginfo

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [AddMissingVehicleInfoPresenterModule::class])
interface AddMissingVehicleInfoComponent {
    fun inject(activity: AddMissingVehicleInfoActivity)
}