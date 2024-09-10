package com.carworkz.dearo.invoices.addItem.labour

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

/**
 * Created by kush on 13/9/17.
 */
@ActivityScoped
@Subcomponent(modules = [(AddLabourPresenterModule::class)])
interface AddLabourComponent {
    fun inject(activity: AddLabourActivity)
}