package com.carworkz.dearo.addjobcard.createjobcard.estimate.digitalsignature

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class DigitalSignaturePresenterModule(val view: DigitalSignatureContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): DigitalSignatureContract.View = view
}