package com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class SearchPartNumberPresenterModule(private val view: SearchPartNumberContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): SearchPartNumberContract.View {
        return view
    }
}