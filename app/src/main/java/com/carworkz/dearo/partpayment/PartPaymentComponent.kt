package com.carworkz.dearo.partpayment

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [PartPaymentPresenterModule::class])
interface PartPaymentComponent {

    fun inject(activity: PartPaymentActivity)
}