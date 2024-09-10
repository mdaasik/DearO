package com.carworkz.dearo.mycustomers.mycustomerlisting

import com.carworkz.dearo.injection.FragmentScoped
import dagger.Subcomponent

@FragmentScoped
@Subcomponent(modules = [CustomerListingPresenterModule::class])
interface CustomerListingComponent {
    fun inject(view: CustomerListingFragment)
}