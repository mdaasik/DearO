package com.carworkz.dearo.vehiclequery

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class VehicleQueryPresenterModule(private val view: VehicleQueryContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): VehicleQueryContract.View {
        return view
    }
}