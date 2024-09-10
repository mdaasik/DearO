package com.carworkz.dearo.addjobcard.createjobcard.accidental.selectionaddress

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [InsuranceAddressSelectionPresenterModule::class])
interface InsuranceAddressSelectionComponent {
    fun inject(activity: InsuranceAddressSelectionActivity)
}