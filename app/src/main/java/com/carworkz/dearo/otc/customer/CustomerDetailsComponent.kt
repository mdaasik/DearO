package com.carworkz.dearo.otc.customer

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [(CustomerDetailsPresenterModule::class)])
interface CustomerDetailsComponent {
    fun inject(OTCCustomerDetailsActivity: OTCCustomerDetailsActivity)
}