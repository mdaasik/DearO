package com.carworkz.dearo.appointments.createappointment.servicePackages

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Module
import dagger.Provides

/**
 * Created by Kush Singh Chibber on 11/25/2017.
 */
@Module
class ServicePackagePresenterModule(val view: ServicePackageContract.View) {
    @Provides
    @FragmentScoped
    fun providesView(): ServicePackageContract.View = view
}