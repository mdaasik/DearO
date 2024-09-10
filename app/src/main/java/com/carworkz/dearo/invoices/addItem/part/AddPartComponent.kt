package com.carworkz.dearo.invoices.addItem.part

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

/**
 * Created by kush on 13/9/17.
 */
@ActivityScoped
@Subcomponent(modules = [(AddPartPresenterModule::class)])
interface AddPartComponent {
    fun inject(activity: AddEditPartActivity)
}