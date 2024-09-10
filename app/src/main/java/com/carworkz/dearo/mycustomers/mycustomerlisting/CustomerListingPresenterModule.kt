package com.carworkz.dearo.mycustomers.mycustomerlisting

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Module
import dagger.Provides

@Module
class CustomerListingPresenterModule(val view: CustomerListingContract.View) {
    @Provides
    @FragmentScoped
    fun providesView(): CustomerListingContract.View {
        return view
    }
}