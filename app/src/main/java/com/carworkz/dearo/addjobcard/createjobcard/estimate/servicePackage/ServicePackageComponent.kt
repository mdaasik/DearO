package com.carworkz.dearo.addjobcard.createjobcard.estimate.servicePackage

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(ServicePackagePresenterModule::class)])
interface ServicePackageComponent {

    fun injects(servicePackageActivity: AddServicePackageActivity)
}