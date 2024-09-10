package com.carworkz.dearo.predeliverycheck

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [PdcModule::class])
interface PdcComponent {

    fun inject(activity: PdcActivity)
}