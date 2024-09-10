package com.carworkz.dearo.addjobcard.createjobcard.accidental

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

@FragmentScoped
@Subcomponent(modules = [AccidentalPresenterModule::class])
interface AccidentalComponent {
    fun inject(accidentalFragment: AccidentalFragment)
}