package com.carworkz.dearo.addjobcard.quickjobcard

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(QuickJobCardPresenterModule::class)])
interface QuickJobCardComponent {

    fun inject(quickJobCardActivity: QuickJobCardActivity)
}