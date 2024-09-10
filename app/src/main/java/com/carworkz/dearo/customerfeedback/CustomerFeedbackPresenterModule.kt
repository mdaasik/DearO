package com.carworkz.dearo.customerfeedback

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class CustomerFeedbackPresenterModule(private val view: CustomerFeedbackContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): CustomerFeedbackContract.View {
        return view
    }
}