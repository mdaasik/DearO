package com.carworkz.dearo.mycustomers.cutomervehiclehistory

import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

@Module
class CustomerVehicleHistoryPresenterModule(val view: CustomerVehicleHistoryContract.View) {
    @Provides
    @ActivityScoped
    fun providesView(): CustomerVehicleHistoryContract.View {
        return view
    }

    @Provides
    @ActivityScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }
}