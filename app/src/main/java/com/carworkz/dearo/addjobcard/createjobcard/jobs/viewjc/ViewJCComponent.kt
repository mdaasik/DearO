package com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

/**
 * Created by kush on 19/9/17.
 */
@ActivityScoped
@Subcomponent(modules = [(ViewJCPresenterModule::class)])
interface ViewJCComponent {
    fun inject(viewJCFragment: ViewJCFragment)
}