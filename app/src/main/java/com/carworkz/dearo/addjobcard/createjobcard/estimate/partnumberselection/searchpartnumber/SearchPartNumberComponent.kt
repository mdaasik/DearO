package com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

@ActivityScoped
@Subcomponent(modules = [SearchPartNumberPresenterModule::class])
interface SearchPartNumberComponent {
    fun inject(activity: SearchPartNumberActivity)
}