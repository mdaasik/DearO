package com.carworkz.dearo.addjobcard.createjobcard.estimate.servicePackage

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class ServicePackagePresenterModule(val view: ServicePackageContract.View) {

    @ActivityScoped
    @Provides
    fun providesView(): ServicePackageContract.View {
        return view
    }
}