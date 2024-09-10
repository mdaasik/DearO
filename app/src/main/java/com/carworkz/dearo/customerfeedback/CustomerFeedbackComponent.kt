package com.carworkz.dearo.customerfeedback

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [CustomerFeedbackPresenterModule::class])
interface CustomerFeedbackComponent {

    fun inject(activity: NewCustomerFeedBackActivity)
}