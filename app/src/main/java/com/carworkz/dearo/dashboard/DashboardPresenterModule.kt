package com.carworkz.dearo.dashboard

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Module
import dagger.Provides

@Module
class DashboardPresenterModule(private val view: DashboardContract.View) {

    @Provides
    @FragmentScoped
    fun providesView(): DashboardContract.View {
        return view
    }
}