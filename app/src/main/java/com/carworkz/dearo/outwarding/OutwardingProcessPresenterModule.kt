package com.carworkz.dearo.outwarding

import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

@Module
class OutwardingProcessPresenterModule(private val view: OutwardingProcessContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): OutwardingProcessContract.View {
        return view
    }

    @Provides
    @ActivityScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }
}