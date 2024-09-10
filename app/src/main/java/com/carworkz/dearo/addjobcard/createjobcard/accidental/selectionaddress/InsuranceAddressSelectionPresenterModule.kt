package com.carworkz.dearo.addjobcard.createjobcard.accidental.selectionaddress

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class InsuranceAddressSelectionPresenterModule(private val view: InsuranceAddressSelectionContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): InsuranceAddressSelectionContract.View {
        return view
    }
}