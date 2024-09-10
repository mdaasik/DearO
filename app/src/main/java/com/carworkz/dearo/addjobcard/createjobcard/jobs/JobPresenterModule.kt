package com.carworkz.dearo.addjobcard.createjobcard.jobs

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Module
import dagger.Provides

/**
 * Created by Farhan on 24/8/17.
 */
@Module
class JobPresenterModule(val view: JobContract.View) {

    @Provides
    @FragmentScoped
    fun providesView(): JobContract.View {
        return view
    }
}