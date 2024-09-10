package com.carworkz.dearo.thirdpartydetails

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class ThirdPartyDetailsPresenterModule(private val view: ThirdPartyDetailsContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): ThirdPartyDetailsContract.View {
        return view
    }
}