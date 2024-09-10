package com.carworkz.dearo.otc

import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

@Module
class OtcProfromaPresenterModule(private val view: OtcProformaContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): OtcProformaContract.View {
        return view
    }

    @Provides
    @ActivityScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }
}