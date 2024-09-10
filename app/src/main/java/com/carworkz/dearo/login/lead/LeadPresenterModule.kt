package com.carworkz.dearo.login.lead

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class LeadPresenterModule(val view: LeadContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): LeadContract.View {
        return view
    }
}