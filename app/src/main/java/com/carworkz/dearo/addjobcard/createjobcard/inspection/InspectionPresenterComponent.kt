package com.carworkz.dearo.addjobcard.createjobcard.inspection

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

/**
 * Created by Farhan on 23/8/17.
 */
@FragmentScoped
@Subcomponent(modules = [(InspectionPresenterModule::class)])
interface InspectionPresenterComponent {

    fun inject(inspectionFragment: InspectionFragment)
}