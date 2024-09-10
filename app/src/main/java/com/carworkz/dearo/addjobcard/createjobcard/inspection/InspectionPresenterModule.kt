package com.carworkz.dearo.addjobcard.createjobcard.inspection

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Module
import dagger.Provides

/**
 * Created by Farhan on 23/8/17.
 */
@Module
class InspectionPresenterModule(private val view: InspectionContract.View, private val isViewOnly: Boolean) {

    @Provides
    @FragmentScoped
    fun providesView(): InspectionContract.View {
        return view
    }

    @Provides
    fun providesIsViewOnly(): Boolean {
        return isViewOnly
    }
}