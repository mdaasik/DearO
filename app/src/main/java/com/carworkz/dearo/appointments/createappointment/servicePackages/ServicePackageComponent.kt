package com.carworkz.dearo.appointments.createappointment.servicePackages

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

/**
 * Created by Kush Singh Chibber on 11/25/2017.
 */
@FragmentScoped
@Subcomponent(modules = [(ServicePackagePresenterModule::class)])
interface ServicePackageComponent {
    fun inject(servicePackageFragment: ServicePackageFragment)
}