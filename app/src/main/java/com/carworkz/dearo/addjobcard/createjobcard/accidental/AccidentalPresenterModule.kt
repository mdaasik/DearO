package com.carworkz.dearo.addjobcard.createjobcard.accidental

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Module
import dagger.Provides

@Module
class AccidentalPresenterModule(private val view: AccidentalContract.View) {

    @Provides
    @FragmentScoped
    fun providesView(): AccidentalContract.View {
        return view
    }
}