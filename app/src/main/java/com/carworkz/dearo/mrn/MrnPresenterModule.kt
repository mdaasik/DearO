package com.carworkz.dearo.mrn

import com.carworkz.dearo.injection.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
class MrnPresenterModule(private val view: MrnContract.View) {

    @Provides
    @ActivityScoped
    fun providesMrn(): MrnContract.View {
        return view
    }
}