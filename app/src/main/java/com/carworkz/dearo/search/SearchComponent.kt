package com.carworkz.dearo.search

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Subcomponent

/**
 * Created by kush on 24/8/17.
 */
@ActivityScoped
@Subcomponent(modules = [(SearchPresenterModule::class)])
interface SearchComponent {
    fun inject(searchActivity: SearchActivity)
}
