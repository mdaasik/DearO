package com.carworkz.dearo.partfinder

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class PartFinderPresenterModule(private val view: PartFinderContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): PartFinderContract.View {
        return view
    }
}