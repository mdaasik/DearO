package com.carworkz.dearo.othersyshistory

import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

@Module
class OtherHistoryPresenterModule(val view: OtherHistoryContract.View) {
    @Provides
    @ActivityScoped
    fun providesView(): OtherHistoryContract.View {
        return view
    }

    @Provides
    @ActivityScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }
}