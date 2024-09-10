package com.carworkz.dearo.otc.customer

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class CustomerDetailsPresenterModule(private val view: CustomerDetailsContract.View) {

    @ActivityScoped
    @Provides
    fun providesView(): CustomerDetailsContract.View {
        return view
    }
}