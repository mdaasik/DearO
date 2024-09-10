package com.carworkz.dearo.otc

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(OtcProfromaPresenterModule::class)])
interface OtcProformaComponent {
    fun inject(activity: OtcProformaActivity)
}