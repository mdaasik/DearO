package com.carworkz.dearo.addjobcard.quickjobcard.quickconcerns

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Module
import dagger.Provides

@Module
class QuickConcernPresenterModule(private val view: QuickConcernsContract.View) {

    @Provides
    @FragmentScoped
    fun providesView(): QuickConcernsContract.View {
        return view
    }
}