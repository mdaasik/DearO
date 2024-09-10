package com.carworkz.dearo.addjobcard.createjobcard.jobs

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

/**
 * Created by Farhan on 24/8/17.
 */
@FragmentScoped
@Subcomponent(modules = [(JobPresenterModule::class)])
interface JobsComponent {

    fun inject(jobsFragment: JobsFragment)
}