package com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

/**
 * Created by kush on 19/9/17.
 */

@Module
class ViewJCPresenterModule(private val view: ViewJCContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): ViewJCContract.View {
        return view
    }
}