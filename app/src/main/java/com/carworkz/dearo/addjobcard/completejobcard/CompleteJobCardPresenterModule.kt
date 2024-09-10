package com.carworkz.dearo.addjobcard.completejobcard

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class CompleteJobCardPresenterModule(private val view: CompleteJobCardContract.View)
{
    @ActivityScoped
    @Provides
    fun providesView(): CompleteJobCardContract.View {
        return view
    }
}