package com.carworkz.dearo.addjobcard.createjobcard.estimate.digitalsignature

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [DigitalSignaturePresenterModule::class])
interface DigitalSignatureComponent {
    fun inject(digitalSignatureActivity: DigitalSignatureActivity)
}