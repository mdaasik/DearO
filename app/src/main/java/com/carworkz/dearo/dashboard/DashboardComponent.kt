package com.carworkz.dearo.dashboard

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

@FragmentScoped
@Subcomponent(modules = [DashboardPresenterModule::class])
interface DashboardComponent {
    fun inject(DashboardFragment: DashboardFragment)
}