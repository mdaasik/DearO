package com.carworkz.dearo.amc.amcsolddetails

import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

@Module
class AmcDetailsPresenterModule(val view: AmcDetailsContract.View) {
    @Provides
    @ActivityScoped
    fun providesView(): AmcDetailsContract.View {
        return view
    }

    @Provides
    @ActivityScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }
}