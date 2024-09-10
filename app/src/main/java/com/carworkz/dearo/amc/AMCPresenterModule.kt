package com.carworkz.dearo.amc

import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

@Module
class AMCPresenterModule(private val view: AMCContract.View)
{
    @Provides
    @ActivityScoped
    fun providesView(): AMCContract.View {
        return view
    }

    @Provides
    @ActivityScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }
}