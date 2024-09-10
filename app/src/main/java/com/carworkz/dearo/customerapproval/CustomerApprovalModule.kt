package com.carworkz.dearo.customerapproval

import com.carworkz.dearo.injection.ActivityScoped
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.pdf.mediator.PdfMediatorImpl
import dagger.Module
import dagger.Provides

@Module
class CustomerApprovalModule(private val view: CustomerApprovalContract.View) {

    @Provides
    @ActivityScoped
    fun providesView(): CustomerApprovalContract.View {
        return view
    }

/*    @Provides
    @ActivityScoped
    fun providesPdfMediator(): PdfMediator {
        return PdfMediatorImpl()
    }*/
}