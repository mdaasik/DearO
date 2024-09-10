package com.carworkz.dearo.addjobcard.quickjobcard.quickconcerns

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

@FragmentScoped
@Subcomponent(modules = [QuickConcernPresenterModule::class])
interface QuickConcernComponent {

    fun inject(quickConcernsFragment: QuickConcernsFragment)
}