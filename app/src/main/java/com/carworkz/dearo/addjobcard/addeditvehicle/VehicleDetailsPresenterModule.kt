package com.carworkz.dearo.addjobcard.addeditvehicle

import com.carworkz.dearo.injection.ActivityScoped

import dagger.Module
import dagger.Provides

/**
 * Created by kush on 18/8/17.
 */
@Module
class VehicleDetailsPresenterModule(private val view: VehicleDetailsContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): VehicleDetailsContract.View {
        return view
    }
}
