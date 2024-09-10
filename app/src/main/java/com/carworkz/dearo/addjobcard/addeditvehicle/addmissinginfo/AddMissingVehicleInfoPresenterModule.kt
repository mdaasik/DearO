package com.carworkz.dearo.addjobcard.addeditvehicle.addmissinginfo

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class AddMissingVehicleInfoPresenterModule(private val view: AddMissingVehicleInfoContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): AddMissingVehicleInfoContract.View {
        return view
    }
}