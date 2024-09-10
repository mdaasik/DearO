package com.carworkz.dearo.mycustomers.cutomervehiclehistory

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [CustomerVehicleHistoryPresenterModule::class])
interface CustomerVehicleHistoryComponent {
    fun inject(view: CustomerVehicleHistoryActivity)
}