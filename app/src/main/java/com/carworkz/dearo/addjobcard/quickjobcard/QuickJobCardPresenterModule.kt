package com.carworkz.dearo.addjobcard.quickjobcard

import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

@Module
class QuickJobCardPresenterModule(val view: QuickJobCardContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): QuickJobCardContract.View {
        return view
    }

    @Provides
    @ActivityScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }
}